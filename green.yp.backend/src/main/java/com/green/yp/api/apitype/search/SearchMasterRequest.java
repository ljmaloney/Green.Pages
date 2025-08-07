package com.green.yp.api.apitype.search;

import com.green.yp.api.apitype.enumeration.SearchRecordType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record SearchMasterRequest(
        @NotNull @NotNull
        UUID externId,
        UUID producerId,
        UUID locationId,
        UUID categoryRef,
        @NotNull @NotNull
        String categoryName,
        @NotNull @NotNull
        SearchRecordType recordType,
        LocalDate lastActiveDate,
        String keywords,
        @NotNull @NotNull
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
        @NotNull @NotNull
        BigDecimal longitude,
        @NotNull @NotNull
        BigDecimal latitude,
        String description) {}
