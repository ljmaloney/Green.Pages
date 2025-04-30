package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.common.enumeration.DayOfWeekType;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LocationHoursResponse(
    @NotNull @NonNull UUID locationHoursId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID producerLocationId,
    @NotNull @NonNull DayOfWeekType dayOfWeek,
    @NotNull @NonNull String openTime,
    @NotNull @NonNull String closeTime) {}
