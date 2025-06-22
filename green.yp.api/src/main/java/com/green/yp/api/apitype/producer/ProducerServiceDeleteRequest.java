package com.green.yp.api.apitype.producer;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

public record ProducerServiceDeleteRequest(@NotNull @NonNull UUID serviceId,
                                           LocalDate discontinueDate) {}
