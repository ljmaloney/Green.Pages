package com.green.yp.api.apitype;

import com.green.yp.api.apitype.enumeration.ServicePriceUnitsType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.NonNull;

public record ProducerServiceResponse(
    @NotNull @NonNull UUID producerServiceId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID producerLocationId,
    Boolean discontinued,
    LocalDate discontinueDate,
    BigDecimal minServicePrice,
    BigDecimal maxServicePrice,
    ServicePriceUnitsType priceUnitsType,
    String shortDescription,
    String description,
    String serviceTerms) {}
