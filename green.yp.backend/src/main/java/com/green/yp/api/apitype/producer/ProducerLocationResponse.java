package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.enumeration.LocationDisplayType;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerLocationResponse(
    @NotNull @NonNull UUID locationId,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    @NotNull @NonNull String locationName,
    @NotNull @NonNull ProducerLocationType locationType,
    @NotNull @NonNull LocationDisplayType locationDisplayType,
    @NotNull @NonNull Boolean active,
    @NotNull @NonNull String addressLine1,
    String addressLine2,
    String addressLine3,
    @NotNull @NonNull String city,
    @NotNull @NonNull String state,
    @NotNull @NonNull String postalCode,
    String latitude,
    String longitude,
    String websiteUrl,
    List<LocationHoursResponse> locationHours) {}
