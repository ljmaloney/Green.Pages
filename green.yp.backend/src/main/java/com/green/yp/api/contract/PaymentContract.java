package com.green.yp.api.contract;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.exception.NotFoundException;
import com.green.yp.payment.service.PaymentMethodService;
import com.green.yp.payment.service.PaymentOrchestrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PaymentContract {

  private final PaymentOrchestrationService orchestrationService;
  private final PaymentMethodService paymentMethodService;

  public PaymentContract(
          PaymentOrchestrationService orchestrationService, PaymentMethodService paymentMethodService) {
    this.orchestrationService = orchestrationService;
      this.paymentMethodService = paymentMethodService;
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

  public Optional<PaymentMethodResponse> getPaymentMethod(@NotNull @NonNull UUID referenceId,
                                           String authenticatedUser,
                                           String requestIP) {
    try{
      return Optional.of(paymentMethodService.findMethod(referenceId.toString(), authenticatedUser, requestIP));
    } catch (NotFoundException nfe){
      return Optional.empty();
    }
  }
}
