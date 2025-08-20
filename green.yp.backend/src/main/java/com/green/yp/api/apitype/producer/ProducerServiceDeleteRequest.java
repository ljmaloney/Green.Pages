package com.green.yp.api.apitype.producer;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.NonNull;

public record ProducerServiceDeleteRequest(@NotNull @NonNull UUID serviceId,
                                           LocalDate discontinueDate) {}
