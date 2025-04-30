package com.green.yp.api.apitype.reference;

import com.green.yp.api.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionApi {
    private UUID subscriptionId;
    private Long version;
    private OffsetDateTime createDate;
    private OffsetDateTime lastUpdateDate;
    @NotNull
    private String displayName;
    @NotNull
    private Date endDate;
    String lineOfBusiness;
    @NotNull BigDecimal monthlyAutopayAmount;
    BigDecimal quarterlyAutopayAmount;
    @NotNull BigDecimal annualBillAmount;
    @NotNull
    private String shortDescription;
    private String htmlDescription;
    @NotNull
    private Date startDate;
    @NotNull
    private SubscriptionType subscriptionType;
    @NotNull
    private Integer sortOrder;
}
