package com.green.yp.api.apitype.payment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;

public record ProducerPaymentMethodRequest(
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull String paymentMethod,
    @NotNull @NonNull String payorName,
    @NotNull @NonNull String payorAddress1,
    String payorAddress2,
    @NotNull @NonNull String payorCity,
    @NotNull @NonNull String payorState,
    @NotNull @NonNull String payorPostalCode) {
  public String getMaskedMethod() {
    if (paymentMethod != null) {
      String lastFour = paymentMethod.substring(paymentMethod.length() - 4);
      String firstTwo = paymentMethod.substring(0, 2);
      return String.format("%s**-**********-%s", firstTwo, lastFour);
    }
    return null;
  }
}
