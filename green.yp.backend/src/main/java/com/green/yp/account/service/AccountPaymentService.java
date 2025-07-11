package com.green.yp.account.service;

import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.ApiPaymentResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.ProducerPaymentResponse;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.api.contract.*;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
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

  private final ProducerInvoiceContract producerInvoiceContract;

  private final ProducerContract producerContract;

  private final PaymentContract paymentContract;

  private final ProducerContactContract contactContract;
  private final ProducerLocationContract locationContract;

  public AccountPaymentService(
      EmailService emailService,
      ProducerInvoiceContract producerInvoiceContract,
      ProducerContract producerContract,
      PaymentContract paymentContract,
      ProducerContactContract contactContract,
      ProducerLocationContract locationContract) {
    this.emailService = emailService;
    this.producerInvoiceContract = producerInvoiceContract;
    this.producerContract = producerContract;
    this.paymentContract = paymentContract;
    this.contactContract = contactContract;
    this.locationContract = locationContract;
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

    // create invoice record for initial payment, as this is the first payment on a
    // new subscriber, no invoice is created until this point
    InvoiceResponse invoiceResponse =
        producerInvoiceContract.createInvoice(paymentRequest.producerId(), requestIP);

    ProducerPaymentResponse producerPaymentResponse =
        paymentContract.applyPayment(
            paymentRequest,
            invoiceResponse.invoiceId(),
            ProducerPaymentType.INITIAL_PAYMENT,
            requestIP);

    if (!producerPaymentResponse.isSuccess()) {
      log.info(
          "Subscription payment was not successful for {} reasonCode {}",
          paymentRequest.producerId(),
          producerPaymentResponse.responseCode());

      return new ApiPaymentResponse(
          producerPaymentResponse.isSuccess(),
          producerPaymentResponse.responseCode(),
          producerPaymentResponse.responseText());
    }

    producerResponse =
        producerContract.activateProducer(
            paymentRequest.producerId(),
            invoiceResponse.createDate(),
            producerPaymentResponse.createDate(),
            null,
            requestIP);

    emailService.sendEmail(
        EmailTemplateType.WELCOME_EMAIL,
        producerResponse,
        getAdminEmails(paymentRequest.producerId()).toArray(new String[0]));

    return new ApiPaymentResponse(
        true, producerPaymentResponse.responseCode(), producerPaymentResponse.responseText());
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
        paymentContract.applyPayment(paymentRequest, userId, requestIP);

    //        emailService.sendEmail(EmailTemplateName.WELCOME_EMAIL, producerResponse,
    //                getAdminEmails(paymentRequest.producerId()).toArray(new String[0]));

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
    List<UUID> producerIds = producers.stream().map(p -> p.producerId()).toList();

    producerContract.deleteCredentials(producerIds);

    contactContract.deleteContacts(producerIds);

    locationContract.deleteLocation(producerIds);

    producerContract.deleteProducers(producerIds, ipAddress);

    log.info("Removed {} unpaid account subscriptions", producers.size());

    return String.format(
        "Removed %s unpaid account subscriptions over %s days old", producerIds.size(), daysOld);
  }

  private List<String> getAdminEmails(UUID accountId) {
    List<ProducerContactResponse> contacts = contactContract.findAdminContacts(accountId);

    return contacts.stream().map(contact -> contact.emailAddress()).toList();
  }
}
