package com.green.yp.api.apitype.reference;

import com.green.yp.api.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public record SubscriptionApi(
    UUID subscriptionId,
    Long version,
    OffsetDateTime createDate,
    OffsetDateTime lastUpdateDate,
    @NotNull String displayName,
    @NotNull Date endDate,
    String lineOfBusiness,
    @NotNull BigDecimal monthlyAutopayAmount,
    BigDecimal quarterlyAutopayAmount,
    @NotNull BigDecimal annualBillAmount,
    @NotNull String shortDescription,
    String htmlDescription,
    @NotNull Date startDate,
    @NotNull SubscriptionType subscriptionType,
    @NotNull Integer sortOrder,
    boolean comingSoon,
    List<String> features) {}
