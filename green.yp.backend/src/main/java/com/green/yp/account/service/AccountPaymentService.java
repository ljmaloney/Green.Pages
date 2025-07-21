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
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.PreconditionFailedException;
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
          EmailService emailService, InvoiceContract invoiceContract,
          ProducerInvoiceContract producerInvoiceContract,
          ProducerContract producerContract, EmailContract emailContract,
          ProducerPaymentContract producerPaymentContract,
          ProducerContactContract contactContract,
          ProducerLocationContract locationContract, PaymentContract paymentContract, AccountPaymentMapper paymentMapper) {
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
      @NotNull @NonNull ApplyPaymentMethodRequest paymentRequest,
      @NotNull @NonNull String requestIP) {
    log.info("Apply initial subscription payment for {}", paymentRequest.producerId());

    ProducerResponse producerResponse = producerContract.findProducer(paymentRequest.producerId());
    if (producerResponse.subscriptionType() == ProducerSubscriptionType.LIVE_ACTIVE) {
      throw new PreconditionFailedException("Producer/Account is already active");
    }

    ProducerContactResponse primaryContact =  contactContract.findAdminContacts(producerResponse.producerId())
            .stream().filter(contact-> contact.producerContactType() == ProducerContactType.ADMIN)
            .findFirst().orElseThrow( () -> {
              log.error("No primary contact found for {}", paymentRequest.producerId());
              return new PreconditionFailedException("No primary contact found for " + paymentRequest.producerId());
            });

    var validation = emailContract.validateEmail(paymentRequest.producerId().toString(), primaryContact.emailAddress());

    if (!validation.validationStatus().isValidated()
        && !paymentRequest.emailValidationToken().equals(validation.token())) {
      log.warn("Email has not been confirmed for the admin contact {} for {}", paymentRequest.producerId(), primaryContact.emailAddress());
      throw new PreconditionFailedException("Admin email address has not been confirmed for the account");
    }

    var invoice = createInvoiceForPayment(paymentRequest, producerResponse);

    var savedCustomerCard = paymentContract.createPaymentMethod(paymentMapper.toPaymentMethod(paymentRequest), requestIP);

    var completedPayment =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, savedCustomerCard, invoice),
                Optional.of(savedCustomerCard.externCustRef()), true);

    invoiceContract.updatePayment(invoice.invoiceId(), completedPayment);

    producerContract.activateProducer(paymentRequest.producerId(),
            completedPayment.createDate(), completedPayment.createDate(), "system", requestIP);

    emailService.sendEmailAsync(EmailTemplateType.PRODUCER_PAYMENT_CONFIRMATION,
            Collections.singletonList(primaryContact.emailAddress()),
            EmailTemplateType.PRODUCER_PAYMENT_CONFIRMATION.getSubjectFormat(),
            () -> Map.of("invoice", invoice,
                    "producerId", producerResponse.producerId(),
                    "lastName", primaryContact.lastName(),
                    "firstName", primaryContact.firstName(),
                    "transactionRef", completedPayment.paymentRef(),
                    "receiptUrl", completedPayment.receiptUrl(),
                    "timestamp", OffsetDateTime.now(),
                    "ipAddress", requestIP) );

    return new ApiPaymentResponse(
        true, completedPayment.receiptNumber(), completedPayment.receiptUrl());
  }

  private InvoiceResponse createInvoiceForPayment(@org.jetbrains.annotations.NotNull ApplyPaymentMethodRequest paymentRequest, ProducerResponse producerResponse) {
    var primarySubscription =
            producerResponse.subscriptions().stream()
                    .filter(sub -> sub.subscriptionType().isPrimarySubscription())
                    .findFirst()
                    .orElseThrow(() -> new PreconditionFailedException( "No primary subscription found for %s", paymentRequest.producerId()));

    List<InvoiceLineItemRequest> lineItems = new ArrayList<>();
    lineItems.add(createLineItem(paymentRequest, primarySubscription, 1));

    List<ProducerSubscriptionResponse> addOnSubscriptions =
            producerResponse.subscriptions().stream()
                    .filter(sub -> !sub.subscriptionType().isPrimarySubscription())
                    .toList();

    lineItems.addAll(addOnSubscriptions.stream()
            .map( addOn -> createLineItem(paymentRequest, addOn, 0))
            .toList());

    return invoiceContract.createInvoice(
        InvoiceRequest.builder()
            .externalRef(paymentRequest.producerId().toString())
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

  private InvoiceLineItemRequest createLineItem(ApplyPaymentMethodRequest paymentRequest,
                                                ProducerSubscriptionResponse subscription,
                                                int lineItemNumber) {
    return InvoiceLineItemRequest.builder()
            .externalRef1(paymentRequest.producerId().toString())
            .externalRef2(subscription.subscriptionId().toString())
            .lineItemNumber(lineItemNumber)
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
}
