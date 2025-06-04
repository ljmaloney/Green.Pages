package com.green.yp.api.apitype.search;

import java.math.BigDecimal;
import java.util.UUID;

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
    String latitude,
    String longitude,
    BigDecimal distance,
    String businessNarrative,
    String iconLink) {}
