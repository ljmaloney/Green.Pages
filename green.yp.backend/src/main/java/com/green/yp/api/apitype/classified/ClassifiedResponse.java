package com.green.yp.api.apitype.classified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ClassifiedResponse(UUID classifiedId,
                                 OffsetDateTime createDate,
                                 OffsetDateTime lastUpdateDate,
                                 LocalDate activeDate,
                                 LocalDate lastActiveDate,
                                 Integer renewalCount,
                                 BigDecimal price,
                                 String perUnitType,
                                 String title,
                                 String description,
                                 String city,
                                 String state,
                                 String postalCode,
                                 String emailAddress,
                                 String phoneNumber,
                                 Double longitude,
                                 Double latitude) {}
