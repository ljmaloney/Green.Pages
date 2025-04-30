package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.LocationDisplayType;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record LocationRequest(
        UUID locationId,
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
        String websiteUrl) {
}
