package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.enumeration.ServicePriceUnitsType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NonNull;

public record ProducerServiceRequest(
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID producerLocationId,
    BigDecimal minServicePrice,
    BigDecimal maxServicePrice,
    ServicePriceUnitsType priceUnitsType,
    String shortDescription,
    String description,
    String serviceTerms) {}
