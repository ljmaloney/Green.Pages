package com.green.yp.account.service;

import com.green.yp.account.mapper.AccountPaymentMapper;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
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
import com.green.yp.exception.PreconditionFailedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountPaymentService {

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
      AccountPaymentMapper paymentMapper) {
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
  }

  /**
   * Applies the first subscription payment and makes the account go "live".
   *
   * @param paymentRequest
   * @param requestIP
   * @return
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public ApiPaymentResponse applyInitialPayment(
      @NotNull @NonNull ApiPaymentRequest paymentRequest, @NotNull @NonNull String requestIP) {
    log.info("Apply initial subscription payment for {}", paymentRequest.referenceId());

    ProducerResponse producerResponse = producerContract.findProducer(paymentRequest.referenceId());
    if (producerResponse.subscriptionType() == ProducerSubscriptionType.LIVE_ACTIVE) {
      throw new PreconditionFailedException("Producer/Account is already active");
    }

    ProducerContactResponse primaryContact = getPrimaryContact(paymentRequest, producerResponse);

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

    var invoice = createInvoiceForPayment(paymentRequest, producerResponse);

    var savedCustomerCard =
        paymentContract.createPaymentMethod(
            paymentMapper.toPaymentMethod(paymentRequest), requestIP);

    var completedPayment =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, savedCustomerCard, invoice),
            Optional.of(savedCustomerCard.externCustRef()),
            true);

    invoiceContract.updatePayment(invoice.invoiceId(), completedPayment);

    producerContract.activateProducer(
        paymentRequest.referenceId(),
        completedPayment.createDate(),
        completedPayment.createDate(),
        "system",
        requestIP);

    sendPaymentCompleted(paymentRequest, requestIP, primaryContact, invoice, producerResponse, completedPayment);

    return new ApiPaymentResponse(
        true, completedPayment.receiptNumber(), completedPayment.receiptUrl());
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
        true, producerPaymentResponse.responseCode(), producerPaymentResponse.responseText());
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
            .getPaymentMethod(paymentRequest.referenceId(), authenticatedUser, requestIP)
            .isEmpty();

    var methodResponse = paymentContract.replaceCardOnFile(
        paymentRequest, authenticatedUser, createNew, requestIP);

    if ( producer.getLastBillPaidDate() == null ) {
      var unpaidInvoice = invoiceContract.findUnpaidInvoice(paymentRequest.referenceId(), authenticatedUser, requestIP)
              .orElseGet( () -> createInvoiceForPayment(paymentRequest, producer));

      var completedPayment =
              paymentContract.applyPayment(
                      paymentMapper.toPaymentRequest(paymentRequest, methodResponse, unpaidInvoice),
                      Optional.of(methodResponse.externCustRef()),
                      true);

      invoiceContract.updatePayment(unpaidInvoice.invoiceId(), completedPayment);

      producerContract.updatePaidDates(paymentRequest.referenceId(),
              completedPayment.createDate(), completedPayment.createDate(), authenticatedUser.userId(), requestIP);

      var primaryContact = getPrimaryContact(paymentRequest, producer);

      sendPaymentCompleted(paymentRequest,requestIP, primaryContact, unpaidInvoice, producer, completedPayment);
    }

    return methodResponse;
  }

  /**
   * The purpose of this method is to clean up records corresponding to any subscriptions started
   * but never finished or made active.
   *
   * @param daysOld
   * @return
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public String cleanUnpaidAccounts(@NotNull @NonNull Integer daysOld, String ipAddress) {
    log.info("Removing unpaid account records and credentials");

    List<ProducerResponse> producers =
        producerContract.findLastModified(daysOld, ProducerSubscriptionType.LIVE_UNPAID);
    if (CollectionUtils.isEmpty(producers)) {
      return String.format("No unpaid subscribers over %s days old", daysOld);
    }
    List<UUID> producerIds = producers.stream().map(ProducerResponse::producerId).toList();

    paymentContract.disablePaymentMethod(producerIds);

    producerContract.deleteCredentials(producerIds);
    log.info("Removed / deleted credentials for {}", producers);

    contactContract.deleteContacts(producerIds);
    log.info("Removed / deleted contacts for {}", producers);

    locationContract.deleteLocation(producerIds);
    log.info("Removed / deleted locations for {}", producers);

    producerContract.deleteProducers(producerIds, ipAddress);
    log.info("Removed / deleted producer records for {}", producers);

    log.info("Removed {} unpaid account subscriptions", producers.size());

    return String.format(
        "Removed %s unpaid account subscriptions over %s days old", producerIds.size(), daysOld);
  }

  private InvoiceResponse createInvoiceForPayment(
      ApiPaymentRequest paymentRequest, ProducerResponse producerResponse) {
    var primarySubscription =
        producerResponse.subscriptions().stream()
            .filter(sub -> sub.subscriptionType().isPrimarySubscription())
            .findFirst()
            .orElseThrow(
                () ->
                    new PreconditionFailedException(
                        "No primary subscription found for %s", paymentRequest.referenceId()));

    List<InvoiceLineItemRequest> lineItems = new ArrayList<>();
    lineItems.add(createLineItem(paymentRequest, primarySubscription, 1));

    List<ProducerSubscriptionResponse> addOnSubscriptions =
        producerResponse.subscriptions().stream()
            .filter(sub -> !sub.subscriptionType().isPrimarySubscription())
            .toList();

    lineItems.addAll(
        addOnSubscriptions.stream()
            .map(addOn -> createLineItem(paymentRequest, addOn, 0))
            .toList());

    return invoiceContract.createInvoice(
        InvoiceRequest.builder()
            .externalRef(paymentRequest.referenceId().toString())
            .invoiceType(InvoiceType.SUBSCRIPTION)
            .description(
                String.format(
                    "%s : Subscription Package %s",
                    producerResponse.businessName(), primarySubscription.displayName()))
            .lineItems(lineItems)
            .invoiceTotal(
                lineItems.stream()
                    .map(InvoiceLineItemRequest::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
            .build());
  }

  private InvoiceLineItemRequest createLineItem(
      ApiPaymentRequest paymentRequest,
      ProducerSubscriptionResponse subscription,
      int lineItemNumber) {
    return InvoiceLineItemRequest.builder()
        .externalRef1(paymentRequest.referenceId().toString())
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
              subscription.shortDescription());
      case ADD_ON, LINE_OF_BUSINESS_ADD_ON ->
          String.format(
              "%s - Additional Services - %s",
              subscription.invoiceCycleType().getCycleDescription(),
              subscription.shortDescription());
    };
  }

  private ProducerContactResponse getPrimaryContact(@NotNull ApiPaymentRequest paymentRequest,
                                                    @NotNull ProducerResponse producerResponse) {
    return contactContract.findAdminContacts(producerResponse.producerId()).stream()
        .filter(contact -> contact.producerContactType() == ProducerContactType.ADMIN)
        .findFirst()
        .orElseThrow(
            () -> {
              log.error("No primary contact found for {}", paymentRequest.referenceId());
              return new PreconditionFailedException(
                  "No primary contact found for " + paymentRequest.referenceId());
            });
  }

  private void sendPaymentCompleted(
      @NotNull ApiPaymentRequest paymentRequest,
      @NotNull String requestIP,
      @NotNull ProducerContactResponse primaryContact,
      @NotNull InvoiceResponse invoice,
      @NotNull ProducerResponse producerResponse,
      @NotNull PaymentTransactionResponse completedPayment) {
    try {
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
                                        requestIP));
                map.put("invoiceLineItems", invoice.lineItems());
                map.put("invoiceTotal", invoice.invoiceTotal());
                return map;
              });
    } catch (Exception e) {
      log.error(
              "Unexpected error sending confirmation email to {} ", paymentRequest.referenceId(), e);
    }
  }
}
