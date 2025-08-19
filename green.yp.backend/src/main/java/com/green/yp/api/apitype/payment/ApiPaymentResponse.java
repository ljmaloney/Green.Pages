package com.green.yp.api.apitype.payment;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record ApiPaymentResponse(
        @NotNull @NonNull Boolean success,
        java.util.UUID referenceId, @NotNull @NonNull String reasonCode,
        @NotNull @NonNull String responseText) {}
