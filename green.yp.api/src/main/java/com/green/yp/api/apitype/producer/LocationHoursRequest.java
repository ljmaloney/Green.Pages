package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.common.enumeration.DayOfWeekType;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.UUID;

public record LocationHoursRequest(
    @NotNull @NonNull UUID locationId,
    @NotNull @NonNull DayOfWeekType dayOfWeek,
    @NotNull @NonNull String openTime,
    @NotNull @NonNull String closeTime) {}
