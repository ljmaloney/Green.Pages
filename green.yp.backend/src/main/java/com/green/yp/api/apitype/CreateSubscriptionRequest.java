package com.green.yp.api.apitype;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateSubscriptionRequest {
  @NotNull @NonNull private BigDecimal annualBillAmount;

  @NotNull
  @NonNull
  @Size(min = 0, max = 30, message = "The service name must be less than 30 characters")
  private String displayName;

  @NotNull @NonNull private Date endDate;

  private String htmlDescription;

  private UUID lineOfBusinessId;

  @NotNull @NonNull private BigDecimal monthlyAutopayAmount;

  @NotNull @NonNull private BigDecimal quarterlyAutopayAmount;

  @NotNull
  @NonNull
  @Size(min = 0, max = 100, message = "The service name must be less than 100 characters")
  private String shortDescription;

  @NotNull @NonNull private Integer sortOrder;

  @NotNull @NonNull private Date startDate;
  @NotNull @NonNull private SubscriptionType subscriptionType;

  private List<String> features;
}
