package com.green.yp.api.apitype.classified;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClassifiedSearchResponse(UUID classifiedId,
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
                                       String imageName,
                                       String url,
                                       BigDecimal longitude,
                                       BigDecimal latitude) {}
