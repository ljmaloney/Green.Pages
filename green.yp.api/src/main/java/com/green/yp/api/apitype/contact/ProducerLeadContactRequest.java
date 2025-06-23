package com.green.yp.api.apitype.contact;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.UUID;

public record ProducerLeadContactRequest(@NotNull @NonNull UUID producerId,
                                         @NotNull @NonNull UUID locationId,
                                         UUID productServiceRef) {}
