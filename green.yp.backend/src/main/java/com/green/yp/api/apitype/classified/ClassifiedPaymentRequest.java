package com.green.yp.api.apitype.classified;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.UUID;

public record ClassifiedPaymentRequest(@NotNull @NonNull UUID classifiedId) {}
