package com.green.yp.api.apitype.search;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

public record SearchLocationUpdateRequest(@NotNull @NonNull UUID externId,
                                          @NotNull @NonNull UUID producerId,
                                          @NotNull @NonNull UUID locationId,
                                          String title,
                                          String businessUrl,
                                          String addressLine1,
                                          String addressLine2,
                                          String city,
                                          String state,
                                          String postalCode,
                                          BigDecimal longitude,
                                          BigDecimal latitude) {}
