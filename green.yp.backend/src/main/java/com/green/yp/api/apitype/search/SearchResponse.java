package com.green.yp.api.apitype.search;

import com.green.yp.api.apitype.enumeration.SearchRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SearchResponse(
        String externId,
        String producerId,
        String locationId,
        String categoryRef,
        String categoryName,
        SearchRecordType recordType,
        Boolean active,
        LocalDate lastActiveDate,
        String keywords,
        String title,
        String businessName,
        String businessUrl,
        String businessIconUrl,
        String imageUrl,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String emailAddress,
        String phoneNumber,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String priceUnitsType,
        Double longitude,
        Double latitude,
        BigDecimal distance,
        String description) {}
