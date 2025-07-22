package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.SystemException;
import com.green.yp.payment.data.model.PaymentTransaction;
import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import com.green.yp.payment.mapper.PaymentTransactionMapper;

import java.util.List;
import java.util.UUID;

import com.squareup.square.core.SquareApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentTransactionService {
  private final PaymentTransactionRepository repository;
  private final PaymentTransactionMapper mapper;

  public PaymentTransactionService(
      PaymentTransactionRepository repository, PaymentTransactionMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentTransaction createPaymentRecord(PaymentRequest paymentRequest) {
    log.info("creating new payment record for token");
    var transaction = mapper.toEntity(paymentRequest);
    return repository.save(transaction);
  }

  @Transactional
  public PaymentTransactionResponse updatePayment(
      UUID transactionId, PaymentResponse cardResponse) {
    log.info("updating payment record {} for completion with response", transactionId);

    var transaction =
        repository
            .findById(transactionId)
            .orElseThrow(
                () -> {
                  log.error("transaction not found for {}", transactionId);
                  return new SystemException(
                      "Missing payment transaction record",
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      ErrorCodeType.SYSTEM_ERROR);
                });

    transaction.setPaymentRef(cardResponse.paymentRef());
    transaction.setCustomerRef(cardResponse.customerRef());
    transaction.setLocationRef(cardResponse.locationRef());
    transaction.setStatus(cardResponse.status());
    transaction.setSourceType(cardResponse.sourceType());
    transaction.setReceiptUrl(cardResponse.receiptUrl());
    transaction.setReceiptNumber(cardResponse.receiptNumber());
    transaction.setStatementDescriptionIdentifier(cardResponse.descriptionId());
    transaction.setCurrencyCode("USD");
    transaction.setOrderRef(cardResponse.orderRef());
    transaction.setPaymentDetails(cardResponse.cardDetails());

    return mapper.fromEntity(repository.save(transaction));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentTransactionResponse updatePaymentError(
          UUID transactionId, SquareApiException apiException) {
    log.info("updating payment record {} for completion with response", transactionId);

    var transaction =
        repository
            .findById(transactionId)
            .orElseThrow(
                () -> {
                  log.error("transaction not found for {}", transactionId);
                  return new SystemException(
                      "Missing payment transaction record",
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      ErrorCodeType.SYSTEM_ERROR);
                });
   var errors = apiException.errors();
   transaction.setStatus("PAYMENT_ERROR");
    transaction.setErrorStatusCode(CollectionUtils.isNotEmpty(errors) ? errors.getFirst().getCode().toString() : "No code");
    transaction.setErrorDetail(CollectionUtils.isNotEmpty(errors) ? errors.getFirst().getDetail().get() : "No Details");
    transaction.setErrorCode(apiException.statusCode());
    transaction.setErrorMessage(apiException.getMessage());
    transaction.setErrorBody(apiException.body().toString());

    return mapper.fromEntity(repository.save(transaction));
  }
}
