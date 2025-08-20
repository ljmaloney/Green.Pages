package com.green.yp.api.apitype.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruncatedProducerResponse(
    UUID producerId,
    UUID producerLocationId,
    String businessName,
    String phone,
    String city,
    String state,
    String postalCode,
    String websiteUrl,
    String iconLink) {}
