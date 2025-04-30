package com.green.yp.api.apitype.reference;

import com.green.yp.api.enumeration.LineOfBusinessCreateType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record LineOfBusinessApi(
    UUID lineOfBusinessId,
    String createByReference,
    OffsetDateTime createDate,
    LineOfBusinessCreateType createType,
    Boolean enableDistanceRadius,
    String description,
    OffsetDateTime lastUpdateDate,
    String lineOfBusiness) {}
