package com.green.yp.api.contract;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.payment.service.PaymentOrchestrationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PaymentContract {

  private final PaymentOrchestrationService orchestrationService;

  public PaymentContract(
          PaymentOrchestrationService orchestrationService) {
    this.orchestrationService = orchestrationService;
  }

  public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef, boolean cardOnFile) {
    return orchestrationService.applyPayment(paymentRequest, customerRef, cardOnFile);
  }

  public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest, String requestIp){
    return orchestrationService.createPaymentMethod(methodRequest, requestIp);
  }

  public void cancelCardOnFile(String referenceId) {
    orchestrationService.cancelCardOnFile(referenceId);
  }

  public PaymentMethodResponse replaceCardOnFile(PaymentMethodRequest methodRequest){
    return orchestrationService.replaceCardOnFile(methodRequest);
  }

  public void disablePaymentMethod(List<UUID> producerIds) {
    producerIds.forEach(orchestrationService::disablePaymentMethods);
  }

  public PaymentMethodResponse replaceCardOnFile(@NotNull @NonNull @Valid ApiPaymentRequest paymentRequest,
                                              @NotNull @NonNull AuthenticatedUser authenticatedUser,
                                              boolean createNew,
                                              @NotNull @NonNull String requestIP) {
      return orchestrationService.replaceCardOnFile(paymentRequest, authenticatedUser, createNew, requestIP);
  }
}
