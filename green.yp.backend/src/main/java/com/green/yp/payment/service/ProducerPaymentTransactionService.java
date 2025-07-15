package com.green.yp.payment.service;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import com.green.yp.api.apitype.payment.ProducerPaymentMethodResponse;
import com.green.yp.payment.data.enumeration.*;
import com.green.yp.payment.data.model.ProducerPaymentTransaction;
import com.green.yp.payment.data.repository.ProducerPaymentTransactionRepository;
import com.green.yp.payment.integration.PaymentIntegrationResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerPaymentTransactionService {

  private final ProducerPaymentTransactionRepository producerPaymentTransactionRepository;

  public ProducerPaymentTransactionService(
      ProducerPaymentTransactionRepository producerPaymentTransactionRepository) {
    this.producerPaymentTransactionRepository = producerPaymentTransactionRepository;
  }

  public ProducerPaymentTransaction createTransaction(
      ProducerInvoiceResponse producerInvoiceResponse,
      ProducerPaymentMethodResponse paymentMethod,
      ProducerPaymentType paymentType,
      PaymentIntegrationResponse response) {

    ProducerPaymentTransaction transaction =
        ProducerPaymentTransaction.builder()
            .paymentMethodId(paymentMethod.paymentMethodId())
            .producerId(producerInvoiceResponse.producerId())
            .invoiceId(producerInvoiceResponse.invoiceId())
            .amount(producerInvoiceResponse.invoiceTotal())
            .paymentType(paymentType)
            .status(PaymentTransactionStatus.SUCCESS)
            .acquirerReferenceNumber(response.acquirerReferenceNumber().toString())
            .avsErrorResponseCode(
                AvsErrorResponseCode.fromErrorCode(response.avsErrorResponseCode()))
            .avsPostalCodeResponseCode(
                AvsResponseCode.fromResponseCode(response.avsPostalCodeResponseCode()))
            .avsStreetAddressResponseCode(
                AvsResponseCode.fromResponseCode(response.avsStreetAddressResponseCode()))
            .cvvResponseCode(CvvResponseCode.fromResponseCode(response.cvvResponseCode()))
            .responseCode(response.responseCode())
            .responseText(response.responseText())
            .build();

    ProducerPaymentTransaction savedTransaction =
        producerPaymentTransactionRepository.saveAndFlush(transaction);

    return savedTransaction;
  }

  public Optional<ProducerPaymentTransaction> findTransaction(
      UUID invoiceId,
      ProducerPaymentType paymentType,
      PaymentTransactionStatus paymentTransactionStatus) {
    Optional<ProducerPaymentTransaction> existingTransaction =
        producerPaymentTransactionRepository.findTransaction(
            invoiceId, paymentType, PaymentTransactionStatus.SUCCESS);
    return existingTransaction;
  }
}
