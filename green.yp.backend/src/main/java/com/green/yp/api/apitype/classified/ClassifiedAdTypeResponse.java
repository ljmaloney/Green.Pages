package com.green.yp.api.apitype.classified;

import com.green.yp.classifieds.data.model.ClassifiedAdFeature;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ClassifiedAdTypeResponse(
    UUID adTypeId,
    OffsetDateTime createDate,
    boolean active,
    String adTypeName,
    BigDecimal monthlyPrice,
    BigDecimal threeMonthPrice,
    ClassifiedAdFeature features) {}
