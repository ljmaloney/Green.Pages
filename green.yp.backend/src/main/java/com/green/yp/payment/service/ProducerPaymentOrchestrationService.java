package com.green.yp.payment.service;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.api.contract.InvoiceContract;
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

  private final InvoiceContract invoiceContract;

  private final ProducerPaymentTransactionRepository producerPaymentTransactionRepository;

  private final ProducerPaymentMethodService producerPaymentMethodService;

  private final ProducerPaymentTransactionService transactionService;

  private final PaymentIntegrationInterface paymentIntegration;

  private final PaymentMapper paymentMapper;

  public ProducerPaymentOrchestrationService(
      InvoiceContract invoiceContract,
      ProducerPaymentTransactionRepository producerPaymentTransactionRepository,
      ProducerPaymentMethodService producerPaymentMethodService,
      ProducerPaymentTransactionService transactionService,
      PaymentIntegrationInterface paymentIntegration,
      PaymentMapper paymentMapper) {
    this.invoiceContract = invoiceContract;
    this.producerPaymentTransactionRepository = producerPaymentTransactionRepository;
    this.producerPaymentMethodService = producerPaymentMethodService;
    this.transactionService = transactionService;
    this.paymentIntegration = paymentIntegration;
    this.paymentMapper = paymentMapper;
  }

  public PaymentResponse applyPayment(
      @NotNull @NonNull ApplyPaymentMethodRequest paymentRequest,
      @NotNull @NonNull UUID invoiceId,
      @NotNull @NonNull ProducerPaymentType paymentType,
      String requestIP) {

    Optional<ProducerPaymentTransaction> transaction =
        transactionService
            .findTransaction(invoiceId, paymentType, PaymentTransactionStatus.SUCCESS)
            .or(
                () -> {
                  InvoiceResponse invoiceResponse =
                      invoiceContract.findInvoice(invoiceId, requestIP);
                  PaymentMethodResponse paymentMethod =
                      producerPaymentMethodService.createPaymentMethod(
                          paymentMapper.toPaymentRequest(paymentRequest));
                  return Optional.ofNullable(
                      applyPayment(invoiceResponse, paymentMethod, paymentType, requestIP));
                });

    return paymentMapper.fromTransaction(transaction.get());
  }

  public PaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {

    Optional<ProducerPaymentTransaction> transaction =
        transactionService
            .findTransaction(
                paymentRequest.invoiceId(),
                paymentRequest.paymentType(),
                PaymentTransactionStatus.SUCCESS)
            .or(
                () -> {
                  InvoiceResponse invoiceResponse =
                      invoiceContract.findInvoice(paymentRequest.invoiceId(), requestIP);
                  PaymentMethodResponse paymentMethod = null;
                  if (paymentRequest.savedPaymentMethodId() != null) {
                    paymentMethod =
                        producerPaymentMethodService.findPaymentMethod(
                            paymentRequest.savedPaymentMethodId());
                  } else if (paymentRequest.newPaymentMethod() != null) {
                    paymentMethod =
                        producerPaymentMethodService.createPaymentMethod(paymentRequest.newPaymentMethod());
                  } else {
                    throw new PreconditionFailedException("Payment must be either saved or new");
                  }
                  return Optional.ofNullable(
                      applyPayment(
                          invoiceResponse, paymentMethod, paymentRequest.paymentType(), requestIP));
                });

    return paymentMapper.fromTransaction(transaction.get());
  }

  private ProducerPaymentTransaction applyPayment(
      InvoiceResponse invoiceResponse,
      PaymentMethodResponse paymentMethod,
      ProducerPaymentType paymentType,
      String requestIP) {
    log.info(
        "Apply payment on invoice {} using method {}",
        invoiceResponse.invoiceId(),
        paymentMethod.paymentMethodId());

    PaymentIntegrationRequest request =
        new PaymentIntegrationRequest(
            invoiceResponse.invoiceId(),
            invoiceResponse.invoiceNumber(),
            "",
            invoiceResponse.invoiceTotal(),
            paymentMethod);

    PaymentIntegrationResponse response = paymentIntegration.applyPayment(request, requestIP);

    ProducerPaymentTransaction transaction =
        transactionService.createTransaction(invoiceResponse, paymentMethod, paymentType, response);

    if (response.isFailed()) {

    } else {
      InvoiceResponse paidInvoice =
          invoiceContract.markInvoicePaid(invoiceResponse.invoiceId(), requestIP);
      // invoiceContract.sendPaidInvoiceEmail(paidInvoice.invoiceId(), paymentMethod.)
    }
    return transaction;
  }
}
