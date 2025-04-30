package com.green.yp.api.contract;

import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import com.green.yp.payment.service.PaymentMethodService;
import com.green.yp.payment.service.PaymentOrchestrationService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentContract {

  private final PaymentOrchestrationService paymentService;

  private final PaymentMethodService methodService;

  public PaymentContract(
      PaymentOrchestrationService paymentService, PaymentMethodService methodService) {
    this.paymentService = paymentService;
    this.methodService = methodService;
  }

  public void cancelBilling(UUID accountId, String userId, String ipAddress) {
    methodService.cancelBilling(accountId, userId, ipAddress);
  }

  public PaymentResponse applyPayment(
      ApplyPaymentMethodRequest paymentRequest,
      UUID invoiceId,
      ProducerPaymentType paymentType,
      String requestIP) {
    return paymentService.applyPayment(paymentRequest, invoiceId, paymentType, requestIP);
  }

  public PaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {
    return paymentService.applyPayment(paymentRequest, userId, requestIP);
  }
}
