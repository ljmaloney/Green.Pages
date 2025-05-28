package com.green.yp.reference.data.model;

import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "subscription", schema = "greenyp")
public class Subscription extends Mutable {

    @NotNull
    @Column(name = "annual_bill_amount")
    BigDecimal annualBillAmount;

    @NotNull
    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Lob
    @Column(name = "html_description")
    private String htmlDescription;

    @Column(name = "line_of_business_id")
    @JoinColumn(referencedColumnName = "line_of_business_id", table = "line_of_business")
    UUID lineOfBusinessId;

    @NotNull
    @Column(name = "monthly_autopay_amount")
    BigDecimal monthlyAutopayAmount;

    @NotNull
    @Column(name = "quarterly_autopay_amount")
    BigDecimal quarterlyAutopayAmount;

    @NotNull
    @Column(name = "short_description", length = 100, nullable = false)
    private String shortDescription;

    @NotNull
    @Column(name = "sort_order")
    private Integer sortOrder;

    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @NotNull
    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubscriptionFeature> features = new ArrayList<>();
}
