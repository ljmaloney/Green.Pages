package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.ProducerProductType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

public record ProducerProductRequest(
        @NotNull @NonNull UUID productId,
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
        Map<String, Object> attributeMap) {
}
