package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.enumeration.ProducerProductType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerProductResponse(
    @NotNull @NonNull UUID productId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID producerLocationId,
    @NotNull @NonNull ProducerProductType productType,
    String botanicalGroup,
    String name,
    BigDecimal price,
    String pricePerType,
    BigInteger availableQuantity,
    String containerSize,
    String description,
    Boolean discontinued,
    LocalDate discontinueDate,
    LocalDate lastOrderDate,
    Map<String, Object> attributes) {}
