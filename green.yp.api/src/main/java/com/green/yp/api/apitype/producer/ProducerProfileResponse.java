package com.green.yp.api.apitype.producer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProducerProfileResponse(
    UUID locationId,
    UUID producerId,
    OffsetDateTime createDate,
    OffsetDateTime lastUpdateDate,
    String locationName,
    String locationType,
    String locationDisplayType,
    boolean active,
    boolean hasImagesUploaded,
    String addressLine1,
    String addressLine2,
    String addressLine3,
    String city,
    String state,
    String postalCode,
    String latitude,
    String longitude,
    String websiteUrl,
    String businessNarrative,
    String iconLink,
    List<LocationHoursResponse> locationHours) {
}
