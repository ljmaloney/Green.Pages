package com.green.yp.api.apitype.producer;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.NonNull;

public record DiscontinueProductRequest(
    @NotNull @NonNull UUID productId,
    @NotNull @NonNull LocalDate discontinueDate,
    @NotNull @NonNull LocalDate lastOrderDate) {}
