package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.ProducerProductType;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public record CreateProductRequest(
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID producerLocationId,
    @NotNull @NonNull ProducerProductType productType,
    String botanicalGroup,
    @NotNull @NonNull String name,
    BigDecimal price,
    String pricePerType,
    BigInteger availableQuantity,
    String containerSize,
    String description,
    Map<String, Object> attributes) {}
