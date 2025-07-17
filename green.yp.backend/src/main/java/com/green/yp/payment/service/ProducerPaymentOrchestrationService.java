package com.green.yp.payment.service;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.ProducerPaymentMethodResponse;
import com.green.yp.api.apitype.payment.ProducerPaymentResponse;
import com.green.yp.api.contract.ProducerInvoiceContract;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.payment.data.enumeration.PaymentTransactionStatus;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import com.green.yp.payment.data.model.ProducerPaymentTransaction;
import com.green.yp.payment.data.repository.ProducerPaymentTransactionRepository;
import com.green.yp.payment.integration.PaymentIntegrationInterface;
import com.green.yp.payment.integration.PaymentIntegrationRequest;
import com.green.yp.payment.integration.PaymentIntegrationResponse;
import com.green.yp.payment.mapper.PaymentMapper;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerPaymentOrchestrationService {

  private final ProducerInvoiceContract producerInvoiceContract;

  private final ProducerPaymentTransactionRepository producerPaymentTransactionRepository;

  private final ProducerPaymentMethodService producerPaymentMethodService;

  private final ProducerPaymentTransactionService transactionService;

  private final PaymentIntegrationInterface paymentIntegration;

  private final PaymentMapper paymentMapper;

  public ProducerPaymentOrchestrationService(
      ProducerInvoiceContract producerInvoiceContract,
      ProducerPaymentTransactionRepository producerPaymentTransactionRepository,
      ProducerPaymentMethodService producerPaymentMethodService,
      ProducerPaymentTransactionService transactionService,
      PaymentIntegrationInterface paymentIntegration,
      PaymentMapper paymentMapper) {
    this.producerInvoiceContract = producerInvoiceContract;
    this.producerPaymentTransactionRepository = producerPaymentTransactionRepository;
    this.producerPaymentMethodService = producerPaymentMethodService;
    this.transactionService = transactionService;
    this.paymentIntegration = paymentIntegration;
    this.paymentMapper = paymentMapper;
  }

  public ProducerPaymentResponse applyPayment(
      @NotNull @NonNull ApplyPaymentMethodRequest paymentRequest,
      @NotNull @NonNull UUID invoiceId,
      @NotNull @NonNull ProducerPaymentType paymentType,
      String requestIP) {

    Optional<ProducerPaymentTransaction> transaction =
        transactionService
            .findTransaction(invoiceId, paymentType, PaymentTransactionStatus.SUCCESS)
            .or(
                () -> {
                  ProducerInvoiceResponse producerInvoiceResponse =
                      producerInvoiceContract.findInvoice(invoiceId, requestIP);
                  ProducerPaymentMethodResponse paymentMethod =
                      producerPaymentMethodService.createPaymentMethod(
                          paymentMapper.toPaymentRequest(paymentRequest));
                  return Optional.ofNullable(
                      applyPayment(producerInvoiceResponse, paymentMethod, paymentType, requestIP));
                });

    return paymentMapper.fromTransaction(transaction.get());
  }

  public ProducerPaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {

    Optional<ProducerPaymentTransaction> transaction =
        transactionService
            .findTransaction(
                paymentRequest.invoiceId(),
                paymentRequest.paymentType(),
                PaymentTransactionStatus.SUCCESS)
            .or(
                () -> {
                  ProducerInvoiceResponse producerInvoiceResponse =
                      producerInvoiceContract.findInvoice(paymentRequest.invoiceId(), requestIP);
                  ProducerPaymentMethodResponse paymentMethod = null;
                  if (paymentRequest.savedPaymentMethodId() != null) {
                    paymentMethod =
                        producerPaymentMethodService.findPaymentMethod(
                            paymentRequest.savedPaymentMethodId());
                  } else if (paymentRequest.newPaymentMethod() != null) {
                    paymentMethod =
                        producerPaymentMethodService.createPaymentMethod(
                            paymentRequest.newPaymentMethod());
                  } else {
                    throw new PreconditionFailedException("Payment must be either saved or new");
                  }
                  return Optional.ofNullable(
                      applyPayment(
                              producerInvoiceResponse, paymentMethod, paymentRequest.paymentType(), requestIP));
                });

    return paymentMapper.fromTransaction(transaction.get());
  }

  private ProducerPaymentTransaction applyPayment(
      ProducerInvoiceResponse producerInvoiceResponse,
      ProducerPaymentMethodResponse paymentMethod,
      ProducerPaymentType paymentType,
      String requestIP) {
    log.info(
        "Apply payment on invoice {} using method {}",
        producerInvoiceResponse.invoiceId(),
        paymentMethod.paymentMethodId());

    PaymentIntegrationRequest request =
        new PaymentIntegrationRequest(
            producerInvoiceResponse.invoiceId(),
            producerInvoiceResponse.invoiceNumber(),
            "",
            producerInvoiceResponse.invoiceTotal(),
            paymentMethod);

    PaymentIntegrationResponse response = paymentIntegration.applyPayment(request, requestIP);

    ProducerPaymentTransaction transaction =
        transactionService.createTransaction(producerInvoiceResponse, paymentMethod, paymentType, response);

    if (response.isFailed()) {

    } else {
      ProducerInvoiceResponse paidInvoice =
          producerInvoiceContract.markInvoicePaid(producerInvoiceResponse.invoiceId(), requestIP);
      // invoiceContract.sendPaidInvoiceEmail(paidInvoice.invoiceId(), paymentMethod.)
    }
    return transaction;
  }
}
