package com.green.yp.api.apitype.payment;

import com.green.yp.payment.data.enumeration.PaymentMethodType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.NonNull;

public record PaymentMethodResponse(
    @NotNull @NonNull UUID paymentMethodId,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull Boolean active,
    OffsetDateTime cancelDate,
    PaymentMethodType methodType,
    @NotNull @NonNull String panLastFour,
    @NotNull @NonNull String paymentMethod,
    @NotNull @NonNull String payorName,
    @NotNull @NonNull String payorAddress1,
    String payorAddress2,
    @NotNull @NonNull String payorCity,
    @NotNull @NonNull String payorState,
    @NotNull @NonNull String payorPostalCode) {}
