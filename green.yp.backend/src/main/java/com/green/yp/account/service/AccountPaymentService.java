package com.green.yp.account.service;

import com.green.yp.account.mapper.AccountPaymentMapper;
import com.green.yp.api.apitype.enumeration.CancelReasonType;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.apitype.enumeration.ProducerSubProcessType;
import com.green.yp.api.apitype.invoice.*;
import com.green.yp.api.apitype.payment.*;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.ProducerSubscriptionResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.api.contract.*;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.PaymentFailedException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.payment.data.enumeration.PaymentMethodStatusType;
import com.green.yp.util.RequestUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountPaymentService {

    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String SYSTEM_USER_ID = "system";
    private final EmailService emailService;

  private final InvoiceContract invoiceContract;
  private final ProducerInvoiceContract producerInvoiceContract;

  private final ProducerContract producerContract;
  private final EmailContract emailContract;

  private final ProducerPaymentContract producerPaymentContract;

  private final ProducerContactContract contactContract;
  private final ProducerLocationContract locationContract;
  private final PaymentContract paymentContract;

  private final AccountPaymentMapper paymentMapper;
    private final SearchContract searchContract;

    public AccountPaymentService(
            EmailService emailService,
            InvoiceContract invoiceContract,
            ProducerInvoiceContract producerInvoiceContract,
            ProducerContract producerContract,
            EmailContract emailContract,
            ProducerPaymentContract producerPaymentContract,
            ProducerContactContract contactContract,
            ProducerLocationContract locationContract,
            PaymentContract paymentContract,
            AccountPaymentMapper paymentMapper, SearchContract searchContract) {
    this.emailService = emailService;
    this.invoiceContract = invoiceContract;
    this.producerInvoiceContract = producerInvoiceContract;
    this.producerContract = producerContract;
    this.emailContract = emailContract;
    this.producerPaymentContract = producerPaymentContract;
    this.contactContract = contactContract;
    this.locationContract = locationContract;
    this.paymentContract = paymentContract;
    this.paymentMapper = paymentMapper;
        this.searchContract = searchContract;
    }

  /**
   * Applies the first subscription payment and makes the account go "live".
   *
   * @param paymentRequest - the normalized payment/biiling data DTO
   * @param requestIP - IP address
   * @return The saved payment data
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public ApiPaymentResponse applyInitialPayment(
      @NotNull @NonNull ApiPaymentRequest paymentRequest, @NotNull @NonNull String requestIP) {
    log.info("Apply initial subscription payment for {}", paymentRequest.referenceId());

    ProducerResponse producerResponse = producerContract.findProducer(paymentRequest.referenceId());
    if (producerResponse.subscriptionType() == ProducerSubscriptionType.LIVE_ACTIVE) {
      throw new PreconditionFailedException("Producer/Account is already active");
    }

    ProducerContactResponse primaryContact = getPrimaryContact(producerResponse);

    var validation =
        emailContract.validateEmail(
            paymentRequest.referenceId().toString(), primaryContact.emailAddress());

    if (!validation.validationStatus().isValidated()
        && !paymentRequest.emailValidationToken().equals(validation.token())) {
      log.warn(
          "Email has not been confirmed for the admin contact {} for {}",
          paymentRequest.referenceId(),
          primaryContact.emailAddress());
      throw new PreconditionFailedException(
          "Admin email address has not been confirmed for the account");
    }

    var invoice = createInvoiceForPayment(producerResponse);

    var savedCustomerCard =
        paymentContract.createPaymentMethod(
            paymentMapper.toPaymentMethod(paymentRequest), requestIP);

    var completedPayment =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, savedCustomerCard, invoice),
            Optional.of(savedCustomerCard.externCustRef()),
            true);

      if ( !PAYMENT_COMPLETED.equals(completedPayment.status()) ) {
          log.warn("Initial subscription payment failed for {} due to {} - {}",
                  paymentRequest.referenceId(),
                  completedPayment.errorStatusCode(),
                  completedPayment.errorDetail());

          sendPaymentFailed( getPrimaryContact(producerResponse), invoice, producerResponse,  completedPayment);
          throw new PaymentFailedException(completedPayment.errorStatusCode(), completedPayment.errorDetail());
      }

    invoiceContract.updatePayment(invoice.invoiceId(), completedPayment);

    producerContract.activateProducer(
        paymentRequest.referenceId(),
        completedPayment.createDate(),
        completedPayment.createDate(),
            SYSTEM_USER_ID,
        requestIP);

    sendPaymentCompleted( requestIP, primaryContact, invoice, producerResponse, completedPayment);

    return new ApiPaymentResponse(
        true, producerResponse.producerId(),
            completedPayment.receiptNumber(),
            completedPayment.receiptUrl());
  }

  public ApiPaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {
    log.info(
        "Apply subscription payment for invoice {} and producer/account {}",
        paymentRequest.invoiceId(),
        paymentRequest.producerId());

    if (paymentRequest.savedPaymentMethodId() == null
        && paymentRequest.newPaymentMethod() == null) {
      throw new PreconditionFailedException(
          "Existing payment method not provided or new payment method specified");
    }

    producerInvoiceContract.findInvoice(paymentRequest.invoiceId(), requestIP);

    ProducerPaymentResponse producerPaymentResponse =
        producerPaymentContract.applyPayment(paymentRequest, userId, requestIP);

    return new ApiPaymentResponse(
        true, paymentRequest.producerId(),
            producerPaymentResponse.responseCode(),
            producerPaymentResponse.responseText());
  }

  public void processSubscriptionPayment(ProducerResponse producer) {
    log.info("Processing monthly subscription payment for {}", producer.producerId());

    if ( producer.cancelDate() != null){
      log.warn("Cannot update payment method for non-active (cancelled) producer {} cancelDate {}",
              producer.producerId(), producer.cancelDate());
      return;
    }

    var paymentMethod = paymentContract
            .getPaymentMethod(producer.producerId(), null, RequestUtil.getRequestIP())
            .filter(pm -> pm.statusType() == PaymentMethodStatusType.CCOF_CREATED)
            .orElse(null);
    if (paymentMethod == null) {
      log.warn("No Saved / Active payment method found for {}", producer.producerId());
      return;
    }

    var invoice = createInvoiceForPayment(producer);

    var completedPayment =
            paymentContract.applyPayment(
                    paymentMapper.toPaymentRequest(producer, paymentMethod, invoice),
                    Optional.of(paymentMethod.externCustRef()),
                    true);

    if ( !PAYMENT_COMPLETED.equals(completedPayment.status()) ) {
      log.warn("Subscription payment failed for {} due to {} - {}",
              producer.producerId(),
              completedPayment.errorStatusCode(),
              completedPayment.errorDetail());
      producerContract.updateProcessStatus(
              producer.producerId(), ProducerSubProcessType.PAYMENT_FAILED);

      producerContract.cancelSubscription(producer.producerId(), CancelReasonType.PAYMENT_FAILED,
              OffsetDateTime.now().plusMonths(1L), completedPayment.errorDetail());

      sendPaymentFailed( getPrimaryContact(producer), invoice, producer,  completedPayment);
      return;
    }

    invoiceContract.updatePayment(invoice.invoiceId(), completedPayment);

    producerContract.updatePaidDates(producer.producerId(),
            invoice.createDate(), completedPayment.createDate(), SYSTEM_USER_ID, SYSTEM_USER_ID);

    producerContract.updateProcessStatus(
        producer.producerId(), ProducerSubProcessType.PAYMENT_SUCCESS);

    var primaryContact = getPrimaryContact(producer);
    sendPaymentCompleted(SYSTEM_USER_ID,primaryContact,invoice, producer, completedPayment);
  }

  public PaymentMethodResponse replacePayment(
      @NotNull @NonNull @Valid ApiPaymentRequest paymentRequest,
      @NotNull @NonNull AuthenticatedUser authenticatedUser,
      @NotNull @NonNull String requestIP) {

    var producer = producerContract.findProducer(paymentRequest.referenceId());

    if ( producer.cancelDate() != null){
      log.info("Cannot update payment method for non-active (cancelled) producer {} cancelDate {}",
              producer.producerId(), producer.cancelDate());
      throw new PreconditionFailedException(
          "Cannot update payment method for non-active (cancelled) producer");
    }

    boolean createNew =
        paymentContract
            .getPaymentMethod(paymentRequest.referenceId(), authenticatedUser.userId(), requestIP)
            .isEmpty();

    var methodResponse = paymentContract.replaceCardOnFile(
        paymentRequest, authenticatedUser, createNew, requestIP);

    if ( producer.lastBillPaidDate() == null || paymentReinstate(producer)) {
      var unpaidInvoice = invoiceContract.findUnpaidInvoice(paymentRequest.referenceId(), authenticatedUser, requestIP)
              .orElseGet( () -> createInvoiceForPayment(producer));

      var completedPayment =
              paymentContract.applyPayment(
                      paymentMapper.toPaymentRequest(paymentRequest, methodResponse, unpaidInvoice),
                      Optional.of(methodResponse.externCustRef()),
                      true);

      if ( !PAYMENT_COMPLETED.equals(completedPayment.status()) ) {
        log.warn("Payment method saved for {} but outstanding invoice could not be paid due to {} - {}",
                paymentRequest.referenceId(),
                completedPayment.errorStatusCode(),
                completedPayment.errorDetail());
        throw new PaymentFailedException(completedPayment.errorStatusCode(), completedPayment.errorDetail());
      }

      invoiceContract.updatePayment(unpaidInvoice.invoiceId(), completedPayment);

      producerContract.updatePaidDates(paymentRequest.referenceId(),
              completedPayment.createDate(), completedPayment.createDate(), authenticatedUser.userId(), requestIP);

      var primaryContact = getPrimaryContact(producer);

      sendPaymentCompleted(requestIP, primaryContact, unpaidInvoice, producer, completedPayment);
    }

    return methodResponse;
  }

    @Async
  public void cleanAbandonedAccounts(int daysOld){
    log.info("Removing unpaid (abandoned signup) account records and credentials");

    List<ProducerResponse> producers = producerContract.findLastModified(daysOld, ProducerSubscriptionType.LIVE_UNPAID, 5);
    while( CollectionUtils.isNotEmpty(producers)) {
      List<UUID> producerIds = producers.stream().map(ProducerResponse::producerId).toList();
      cleanupAbandonedProducers(RequestUtil.getRequestIP(), producerIds);
      producers = producerContract.findLastModified(daysOld, ProducerSubscriptionType.LIVE_UNPAID, 5);
    }
    log.info("Removed unpaid (abandoned signup) account records and credentials");
  }

  /**
   * The purpose of this method is to clean up records corresponding to any subscriptions started
   * but never finished or made active.
   *
   * @param daysOld - removes abandoned/no initial payment accounts
   * @return Amount of abandoned signup removed
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public String cleanAbandonedAccounts(@NotNull @NonNull Integer daysOld, String ipAddress) {
    log.info("Removing unpaid (abandoned signup) account records and credentials");

    List<ProducerResponse> producers =
        producerContract.findLastModified(daysOld, ProducerSubscriptionType.LIVE_UNPAID);
    if (CollectionUtils.isEmpty(producers)) {
      return String.format("No unpaid subscribers over %s days old", daysOld);
    }
    List<UUID> producerIds = producers.stream().map(ProducerResponse::producerId).toList();

    cleanupAbandonedProducers(ipAddress, producerIds);

    return String.format(
        "Removed %s unpaid account subscriptions over %s days old", producerIds.size(), daysOld);
  }

  private void cleanupAbandonedProducers(String ipAddress, List<UUID> producerIds) {
    paymentContract.disablePaymentMethod(producerIds);

    producerContract.deleteCredentials(producerIds);
    log.info("Removed / deleted credentials for {}", producerIds);

    contactContract.deleteContacts(producerIds);
    log.info("Removed / deleted contacts for {}", producerIds);

    locationContract.deleteLocation(producerIds);
    log.info("Removed / deleted locations for {}", producerIds);

    producerContract.deleteProducers(producerIds, ipAddress);

    searchContract.deleteSearchMaster(producerIds, ipAddress);

    log.info("Removed / deleted producer records for {}", producerIds);

    log.info("Removed {} unpaid account subscriptions", producerIds.size());
  }

  private InvoiceResponse createInvoiceForPayment(ProducerResponse producerResponse) {
    var primarySubscription =
        producerResponse.subscriptions().stream()
            .filter(sub -> sub.subscriptionType().isPrimarySubscription())
            .findFirst()
            .orElseThrow(
                () ->
                    new PreconditionFailedException(
                        "No primary subscription found for %s", producerResponse.producerId()));

    List<InvoiceLineItemRequest> lineItems = new ArrayList<>();
    lineItems.add(createLineItem(producerResponse.producerId(), primarySubscription, 1));

    List<ProducerSubscriptionResponse> addOnSubscriptions =
        producerResponse.subscriptions().stream()
            .filter(sub -> !sub.subscriptionType().isPrimarySubscription())
            .toList();

    lineItems.addAll(
        addOnSubscriptions.stream()
            .map(addOn -> createLineItem(producerResponse.producerId(), addOn, 0))
            .toList());

    return invoiceContract.createInvoice(
        InvoiceRequest.builder()
            .externalRef(producerResponse.producerId().toString())
            .invoiceType(InvoiceType.SUBSCRIPTION)
            .description(
                String.format(
                    "%s : %s",
                    producerResponse.businessName(), primarySubscription.displayName()))
            .lineItems(lineItems)
            .invoiceTotal(
                lineItems.stream()
                    .map(InvoiceLineItemRequest::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
            .build());
  }

  private InvoiceLineItemRequest createLineItem(
      UUID producerId,
      ProducerSubscriptionResponse subscription,
      int lineItemNumber) {
    return InvoiceLineItemRequest.builder()
        .externalRef1(producerId.toString())
        .externalRef2(subscription.subscriptionId().toString())
        .lineItemNumber(lineItemNumber)
        .quantity(1)
        .description(getLineItemDescription(subscription))
        .amount(subscription.subscriptionAmount())
        .build();
  }

  private String getLineItemDescription(ProducerSubscriptionResponse subscription) {
    return switch (subscription.subscriptionType()) {
      case DATA_IMPORT_NO_DISPLAY, TOP_LEVEL, LINE_OF_BUSINESS ->
          String.format(
              "%s - %s",
              subscription.invoiceCycleType().getCycleDescription(),
              subscription.displayName());
      case ADD_ON, LINE_OF_BUSINESS_ADD_ON ->
          String.format(
              "%s - Additional Services - %s",
              subscription.invoiceCycleType().getCycleDescription(),
              subscription.displayName());
    };
  }

  private ProducerContactResponse getPrimaryContact(@NotNull ProducerResponse producerResponse) {
    return contactContract.findAdminContacts(producerResponse.producerId()).stream()
        .filter(contact -> contact.producerContactType() == ProducerContactType.ADMIN)
        .findFirst()
        .orElseThrow(
            () -> {
              log.error("No primary contact found for {}", producerResponse.producerId());
              return new PreconditionFailedException(
                  "No primary contact found for " + producerResponse.producerId());
            });
  }

    private boolean paymentReinstate(ProducerResponse producer) {
       return producer.cancelDate() != null && producer.cancelReason() == CancelReasonType.PAYMENT_FAILED;
    }

  private void sendPaymentCompleted(
      @NotNull String requestIP,
      @NotNull ProducerContactResponse primaryContact,
      @NotNull InvoiceResponse invoice,
      @NotNull ProducerResponse producerResponse,
      @NotNull PaymentTransactionResponse completedPayment) {
    try {
      log.debug("Sending payment completed email for {}", producerResponse.producerId());
      emailService.sendEmailAsync(
              EmailTemplateType.PRODUCER_PAYMENT_CONFIRMATION,
              Collections.singletonList(primaryContact.emailAddress()),
              EmailTemplateType.PRODUCER_PAYMENT_CONFIRMATION.getSubjectFormat(),
              () -> {
                Map<String, Object> map =
                        new HashMap<>(
                                Map.of(
                                        "invoice",
                                        invoice,
                                        "invoiceNumber",
                                        invoice.invoiceNumber(),
                                        "invoiceDescription",
                                        invoice.description(),
                                        "producerId",
                                        producerResponse.producerId(),
                                        "lastName",
                                        primaryContact.lastName(),
                                        "firstName",
                                        primaryContact.firstName(),
                                        "transactionRef",
                                        completedPayment.paymentRef(),
                                        "receiptUrl",
                                        completedPayment.receiptUrl(),
                                        "timestamp",
                                        OffsetDateTime.now(),
                                        "ipAddress",
                                        StringUtils.isNotBlank(requestIP) ? requestIP : SYSTEM_USER_ID));
                map.put("invoiceLineItems", invoice.lineItems());
                map.put("invoiceTotal", invoice.invoiceTotal());
                return map;
              });
    } catch (Exception e) {
      log.error(
              "Unexpected error sending confirmation email to {} ", producerResponse.producerId(), e);
    }
  }
  private void sendPaymentFailed(
          @NotNull ProducerContactResponse primaryContact,
          @NotNull InvoiceResponse invoice,
          @NotNull ProducerResponse producerResponse,
          @NotNull PaymentTransactionResponse completedPayment) {
    try {
      emailService.sendEmailAsync(
              EmailTemplateType.PRODUCER_PAYMENT_FAILED,
              Collections.singletonList(primaryContact.emailAddress()),
              EmailTemplateType.PRODUCER_PAYMENT_FAILED.getSubjectFormat(),
              () -> {
                Map<String, Object> map =
                        new HashMap<>(
                                Map.of(
                                        "invoice",
                                        invoice,
                                        "invoiceNumber",
                                        invoice.invoiceNumber(),
                                        "invoiceDescription",
                                        invoice.description(),
                                        "producerId",
                                        producerResponse.producerId(),
                                        "lastName",
                                        primaryContact.lastName(),
                                        "firstName",
                                        primaryContact.firstName(),
                                        "transactionRef",
                                        completedPayment.paymentRef(),
                                        "receiptUrl",
                                        completedPayment.receiptUrl(),
                                        "timestamp",
                                        OffsetDateTime.now()));
                map.put("errorCode", completedPayment.errorStatusCode());
                map.put("errorDetail", completedPayment.errorDetail());
                map.put("invoiceLineItems", invoice.lineItems());
                map.put("invoiceTotal", invoice.invoiceTotal());
                return map;
              });
    } catch (Exception e) {
      log.error(
              "Unexpected error sending confirmation email to {} ", producerResponse.producerId(), e);
    }
  }

}
