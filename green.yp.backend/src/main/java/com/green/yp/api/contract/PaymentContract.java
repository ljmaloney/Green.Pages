package com.green.yp.api.contract;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import com.green.yp.payment.service.PaymentOrchestrationService;
import com.green.yp.payment.service.ProducerPaymentMethodService;
import com.green.yp.payment.service.ProducerPaymentOrchestrationService;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentContract {

  private final PaymentOrchestrationService transactionService;

  private final ProducerPaymentOrchestrationService paymentService;

  private final ProducerPaymentMethodService methodService;

  public PaymentContract(
          ProducerPaymentOrchestrationService paymentService,
          PaymentOrchestrationService transactionService,
          ProducerPaymentMethodService methodService) {
    this.paymentService = paymentService;
    this.transactionService = transactionService;
    this.methodService = methodService;
  }

  public void cancelBilling(UUID accountId, String userId, String ipAddress) {
    methodService.cancelBilling(accountId, userId, ipAddress);
  }

  public ProducerPaymentResponse applyPayment(
      ApplyPaymentMethodRequest paymentRequest,
      UUID invoiceId,
      ProducerPaymentType paymentType,
      String requestIP) {
    return paymentService.applyPayment(paymentRequest, invoiceId, paymentType, requestIP);
  }

  public ProducerPaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {
    return paymentService.applyPayment(paymentRequest, userId, requestIP);
  }

    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
      return transactionService.applyPayment(paymentRequest, customerRef);
    }

}
