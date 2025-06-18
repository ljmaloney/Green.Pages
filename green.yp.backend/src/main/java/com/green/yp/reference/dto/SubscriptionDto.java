package com.green.yp.reference.dto;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
    UUID subscriptionId,
    Long version,
    OffsetDateTime createDate,
    OffsetDateTime lastUpdateDate,
    @NotNull String displayName,
    @NotNull Date endDate,
    UUID lineOfBusinessId,
    @NotNull BigDecimal monthlyAutopayAmount,
    BigDecimal quarterlyAutopayAmount,
    @NotNull BigDecimal annualBillAmount,
    @NotNull String shortDescription,
    String htmlDescription,
    @NotNull Date startDate,
    @NotNull SubscriptionType subscriptionType,
    @NotNull boolean comingSoon,
    @NotNull Integer sortOrder,
    List<SubscriptionFeatureDto> features) {}
