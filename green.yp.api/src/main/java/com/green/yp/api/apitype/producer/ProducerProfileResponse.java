package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerProfileResponse(
    UUID locationId,
    UUID producerId,
    OffsetDateTime createDate,
    OffsetDateTime lastUpdateDate,
    String businessName,
    String locationName,
    String locationType,
    String locationDisplayType,
    boolean hasImagesUploaded,
    String addressLine1,
    String addressLine2,
    String addressLine3,
    String city,
    String state,
    String postalCode,
    String phone,
    String cellPhone,
    BigDecimal latitude,
    BigDecimal longitude,
    String emailAddress,
    String websiteUrl,
    String businessNarrative,
    String iconLink,
    List<LocationHoursResponse> locationHours) {
}
