package com.green.yp.api.apitype.payment;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record ApiPaymentResponse(
    @NotNull @NonNull Boolean success,
    @NotNull @NonNull String reasonCode,
    @NotNull @NonNull String responseText) {}
