package com.green.yp.api.apitype;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public record CreateSubscriptionRequest(
    @NotNull
        @NonNull
        @Size(min = 0, max = 30, message = "The service name must be less than 30 characters")
        String displayName,
    @NotNull @NonNull SubscriptionType subscriptionType,
    @NotNull @NonNull Date startDate,
    @NotNull @NonNull Date endDate,
    String htmlDescription,
    UUID lineOfBusinessId,
    @NotNull @NonNull BigDecimal annualBillAmount,
    @NotNull @NonNull BigDecimal monthlyAutopayAmount,
    @NotNull @NonNull BigDecimal quarterlyAutopayAmount,
    @NotNull
        @NonNull
        @Size(min = 0, max = 100, message = "The service name must be less than 100 characters")
        String shortDescription,
    @NotNull @NonNull Integer sortOrder,
    boolean comingSoon,
    List<CreateSubscriptionFeatureRequest> features) {}
