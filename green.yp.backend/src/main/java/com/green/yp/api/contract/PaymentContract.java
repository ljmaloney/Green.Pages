package com.green.yp.api.contract;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.payment.service.PaymentOrchestrationService;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class PaymentContract {

  private final PaymentOrchestrationService orchestrationService;

  public PaymentContract(
          PaymentOrchestrationService orchestrationService) {
    this.orchestrationService = orchestrationService;
  }

  public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
    return orchestrationService.applyPayment(paymentRequest, customerRef);
  }

  public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest){
    return orchestrationService.createPaymentMethod(methodRequest);
  }

  public void cancelCardOnFile(String referenceId) {
    orchestrationService.cancelCardOnFile(referenceId);
  }

  public PaymentMethodResponse replaceCardOnFile(PaymentMethodRequest methodRequest){
    return orchestrationService.replaceCardOnFile(methodRequest);
  }
}
