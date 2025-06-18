package com.green.yp.api.apitype;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public record UpdateSubscriptionRequest(
    @NotNull @NonNull UUID subscriptionId,
    UUID lineOfBusinessId,
    @NotNull @NonNull SubscriptionType subscriptionType,
    @NotNull @NonNull  @Size(min = 0, max = 30, message = "The service name must be less than 30 characters")
    String displayName,
    @NotNull Date endDate,
    String lineOfBusiness,
    @NotNull BigDecimal monthlyAutopayAmount,
    BigDecimal quarterlyAutopayAmount,
    @NotNull BigDecimal annualBillAmount,
    @NotNull String shortDescription,
    String htmlDescription,
    @NotNull Date startDate,
    @NotNull Integer sortOrder,
    boolean comingSoon,
    List<UpdateSubscriptionFeatureRequest> features) {}
