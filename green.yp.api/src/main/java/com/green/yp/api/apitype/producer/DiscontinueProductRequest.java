package com.green.yp.api.apitype.producer;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

public record DiscontinueProductRequest(
    @NotNull @NonNull UUID productId,
    @NotNull @NonNull LocalDate discontinueDate,
    @NotNull @NonNull LocalDate lastOrderDate) {}
