package com.green.yp.payment.service;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.payment.data.enumeration.*;
import com.green.yp.payment.data.model.PaymentTransaction;
import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import com.green.yp.payment.integration.PaymentIntegrationResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentTransactionService {

  private final PaymentTransactionRepository paymentTransactionRepository;

  public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository) {
    this.paymentTransactionRepository = paymentTransactionRepository;
  }

  public PaymentTransaction createTransaction(
      InvoiceResponse invoiceResponse,
      PaymentMethodResponse paymentMethod,
      ProducerPaymentType paymentType,
      PaymentIntegrationResponse response) {

    PaymentTransaction transaction =
        PaymentTransaction.builder()
            .paymentMethodId(paymentMethod.paymentMethodId())
            .producerId(invoiceResponse.producerId())
            .invoiceId(invoiceResponse.invoiceId())
            .amount(invoiceResponse.invoiceTotal())
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

    PaymentTransaction savedTransaction = paymentTransactionRepository.saveAndFlush(transaction);

    return savedTransaction;
  }

  public Optional<PaymentTransaction> findTransaction(
      UUID invoiceId,
      ProducerPaymentType paymentType,
      PaymentTransactionStatus paymentTransactionStatus) {
    Optional<PaymentTransaction> existingTransaction =
        paymentTransactionRepository.findTransaction(
            invoiceId, paymentType, PaymentTransactionStatus.SUCCESS);
    return existingTransaction;
  }
}
