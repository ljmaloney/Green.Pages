package com.green.yp.api.apitype.classified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClassifiedSearchResponse(
    UUID classifiedId,
    LocalDate activeDate,
    LocalDate lastActiveDate,
    UUID adTypeId,
    UUID categoryId,
    String categoryName,
    BigDecimal price,
    String perUnitType,
    String title,
    String description,
    String city,
    String state,
    String postalCode,
    String emailAddress,
    String phoneNumber,
    Boolean obscureContactInfo,
    String imageName,
    String url,
    BigDecimal longitude,
    BigDecimal latitude,
    BigDecimal distance) {}
