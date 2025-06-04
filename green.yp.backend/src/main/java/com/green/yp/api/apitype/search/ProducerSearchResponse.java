package com.green.yp.api.apitype.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerSearchResponse(
    UUID producerId,
    UUID producerLocationId,
    String businessName,
    String phone,
    String cellPhone,
    String addressLine1,
    String addressLine2,
    String addressLine3,
    String city,
    String state,
    String postalCode,
    String websiteUrl,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal distance,
    String businessNarrative,
    String iconLink) {}
