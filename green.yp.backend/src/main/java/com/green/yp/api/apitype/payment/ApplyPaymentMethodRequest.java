package com.green.yp.api.apitype.payment;

import com.green.yp.api.apitype.common.enumeration.PaymentActionType;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;

public record ApplyPaymentMethodRequest(
        @NotNull @NonNull UUID producerId,
        String paymentMethod,
        @NotNull @NonNull String payorName,
        @NotNull @NonNull String payorAddress1,
        String payorAddress2,
        @NotNull @NonNull String payorCity,
        @NotNull @NonNull String payorState,
        @NotNull @NonNull String payorPostalCode,
        @NotNull @NonNull PaymentActionType actionType,
        @NotNull @NonNull InvoiceCycleType cycleType,
        @NotNull @NonNull String paymentToken,
        @NotNull @NonNull String verificationToken,
        @NotBlank String emailValidationToken) {}
