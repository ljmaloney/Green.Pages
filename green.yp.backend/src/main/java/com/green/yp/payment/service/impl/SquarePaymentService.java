package com.green.yp.payment.service.impl;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.SystemException;
import com.green.yp.payment.service.PaymentService;
import com.squareup.square.SquareClient;
import com.squareup.square.types.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "greenyp.payment.service.impl",
    havingValue = "square",
    matchIfMissing = true)
public class SquarePaymentService implements PaymentService {
    public static final String ERROR_CREATING_NEW_SUBSCRIBER_CUSTOMER = "Error creating new subscriber customer";
    private final SquareResponseMapper squareResponseMapper;

  private final SquareClient squareClient;
  private final SquareResponseMapper responseMapper;

  public SquarePaymentService(
      SquareClient squareClient,
      SquareResponseMapper responseMapper,
      SquareResponseMapper squareResponseMapper) {
    this.squareClient = squareClient;
    this.responseMapper = responseMapper;
    this.squareResponseMapper = squareResponseMapper;
  }

  @Override
  public PaymentResponse processPayment(
      PaymentRequest paymentRequest,
      UUID paymentTransactionId,
      Optional<String> customerRef,
      boolean cardOnFile) {
    log.debug(
        "Processing payment for ref {} amount {}",
        paymentTransactionId,
        paymentRequest.paymentAmount());
    var squarePayment =
        CreatePaymentRequest.builder()
            .sourceId(paymentRequest.paymentToken())
            .idempotencyKey(paymentTransactionId.toString())
            .autocomplete(true)
            .referenceId(paymentRequest.referenceId())
//            .verificationToken(Optional.ofNullable(paymentRequest.verificationToken()))
            .verificationToken(
                cardOnFile ? Optional.empty() : Optional.ofNullable(paymentRequest.verificationToken()))
            .note(paymentRequest.note())
            .statementDescriptionIdentifier(
                StringUtils.truncate(paymentRequest.statementDescription(), 20))
            .buyerEmailAddress(paymentRequest.emailAddress())
            .buyerPhoneNumber(paymentRequest.phoneNumber())
            .amountMoney(createMoney(paymentRequest.paymentAmount()))
            .appFeeMoney(createMoney(BigDecimal.ZERO))
            .tipMoney(createMoney(BigDecimal.ZERO))
            .billingAddress(
                Address.builder()
                    .addressLine1(paymentRequest.address())
                    .locality(paymentRequest.city())
                    .administrativeDistrictLevel1(paymentRequest.state())
                    .postalCode(paymentRequest.postalCode())
                    .firstName(paymentRequest.firstName())
                    .lastName(paymentRequest.lastName())
                    .build());

    customerRef.ifPresent(squarePayment::customerId);

    var squarePaymentResponse = squareClient.payments().create(squarePayment.build());

    return squarePaymentResponse
        .getPayment()
        .map(responseMapper::toPaymentResponse)
        .orElseThrow(
            () -> {
              log.warn(
                  "There was an error when attempting to process payment {}", paymentTransactionId);
              return new SystemException(
                  "Error processing payment",
                  HttpStatus.INTERNAL_SERVER_ERROR,
                  ErrorCodeType.PAYMENT_CUSTOMER_ERROR);
            });
  }

  @Override
  public PaymentCustomerResponse createCustomer(
      PaymentMethodRequest methodRequest, UUID paymentMethodId) {
    log.debug("Creating new customer for paymentMethodId {}", paymentMethodId);
    var squareCustomer =
        CreateCustomerRequest.builder()
            .idempotencyKey(paymentMethodId.toString())
            .companyName(
                StringUtils.isNotBlank(methodRequest.companyName())
                    ? Optional.of(methodRequest.companyName())
                    : Optional.empty())
            .givenName(methodRequest.firstName())
            .familyName(methodRequest.lastName())
            .emailAddress(methodRequest.emailAddress())
            .phoneNumber(methodRequest.phoneNumber())
            .address(createAddress(methodRequest))
            .referenceId(methodRequest.referenceId())
            .build();

    var custResponse = squareClient.customers().create(squareCustomer);

    return custResponse
        .getCustomer()
        .map(squareResponseMapper::toPaymentCustomerResponse)
        .orElseThrow(
            () -> {
              log.warn(
                  "There was an error creating customer for paymentMethodId {}", paymentMethodId);
              return new SystemException(
                      ERROR_CREATING_NEW_SUBSCRIBER_CUSTOMER,
                  HttpStatus.INTERNAL_SERVER_ERROR,
                  ErrorCodeType.PAYMENT_CUSTOMER_ERROR);
            });
  }

  @Override
  public PaymentCustomerResponse updateCustomer(
      PaymentMethodRequest methodRequest, String externCustId, UUID paymentMethodId) {
    log.debug(
        "Updating existing customer for custId {}, referenceId {}",
        externCustId,
        methodRequest.referenceId());

    var custResponse =
        squareClient
            .customers()
            .update(
                UpdateCustomerRequest.builder()
                    .customerId(externCustId)
                    .referenceId(methodRequest.referenceId())
                    .companyName(methodRequest.companyName())
                    .givenName(methodRequest.firstName())
                    .familyName(methodRequest.lastName())
                    .emailAddress(methodRequest.emailAddress())
                    .phoneNumber(methodRequest.phoneNumber())
                    .address(createAddress(methodRequest))
                    .build());

    return custResponse
        .getCustomer()
        .map(squareResponseMapper::toPaymentCustomerResponse)
        .orElseThrow(
            () -> {
              log.warn(
                  "There was an error updating customer for paymentMethodId {}", paymentMethodId);
              return new SystemException(
                      ERROR_CREATING_NEW_SUBSCRIBER_CUSTOMER,
                  HttpStatus.INTERNAL_SERVER_ERROR,
                  ErrorCodeType.PAYMENT_CUSTOMER_ERROR);
            });
  }

  @Override
  public void deactivateExistingCard(String cardRef) {
    log.info("Deactivating existing card for cardRef {}", cardRef);
    squareClient.cards().disable(DisableCardsRequest.builder().cardId(cardRef).build());
    log.info("Deactivated existing card for cardRef {}", cardRef);
  }

  @Override
  public void deleteCustomer(String externCustomerRef) {
    log.debug("Deleting existing customer for customerRef {}", externCustomerRef);
    squareClient
        .customers()
        .delete(DeleteCustomersRequest.builder().customerId(externCustomerRef).build());
  }

  @Override
  public PaymentSavedCardResponse createCardOnFile(
      PaymentMethodRequest methodRequest, String externCustId, UUID paymentMethodId) {
    log.debug("Creating new card on file for customer {}", externCustId);

    var cards =
        squareClient
            .cards()
            .list(
                ListCardsRequest.builder()
                    .customerId(externCustId)
                    .includeDisabled(false)
                    .referenceId(methodRequest.referenceId())
                    .build());

    cards.getItems().forEach(card -> deactivateExistingCard(card.getId().get()));

    log.info("PaymentMethodRequest - {}", methodRequest);

    var squareCard =
        CreateCardRequest.builder()
            .idempotencyKey(paymentMethodId.toString())
            .sourceId(methodRequest.paymentToken())
            .card(
                Card.builder()
                    .customerId(externCustId)
                    .referenceId(methodRequest.referenceId())
                    .cardholderName(
                        String.join(" ", methodRequest.firstName(), methodRequest.lastName()))
                    .billingAddress(createAddress(methodRequest))
                    .build())
            .verificationToken(methodRequest.verificationToken())
            .build();

    log.debug("Square request data :  {}", squareCard);

    var squareCardResponse = squareClient.cards().create(squareCard);

    return squareCardResponse
        .getCard()
        .map(squareResponseMapper::toSavedCardResponse)
        .orElseThrow(
            () -> {
              log.warn(
                  "There was an error creating card on file for customer for paymentMethodId {}", paymentMethodId);
              return new SystemException(
                      ERROR_CREATING_NEW_SUBSCRIBER_CUSTOMER,
                  HttpStatus.INTERNAL_SERVER_ERROR,
                  ErrorCodeType.PAYMENT_CUSTOMER_ERROR);
            });
  }

  private Money createMoney(BigDecimal amount) {
    return Money.builder()
        .amount(convertToCents(amount).longValue())
        .currency(Currency.USD)
        .build();
  }

  @NotNull
  private static Address createAddress(PaymentMethodRequest methodRequest) {
    return Address.builder()
        .addressLine1(methodRequest.payorAddress1())
        .addressLine2(
            StringUtils.isNotBlank(methodRequest.payorAddress2())
                ? Optional.of(methodRequest.payorAddress2())
                : Optional.empty())
        .locality(methodRequest.payorCity())
        .administrativeDistrictLevel1(methodRequest.payorState())
        .postalCode(methodRequest.payorPostalCode())
        .firstName(methodRequest.firstName())
        .lastName(methodRequest.lastName())
        .country(Country.US)
        .build();
  }
}
