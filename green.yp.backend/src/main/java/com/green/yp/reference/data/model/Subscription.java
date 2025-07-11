package com.green.yp.reference.data.model;

import com.green.yp.api.apitype.UpdateSubscriptionFeatureRequest;
import com.green.yp.common.data.converter.BooleanConverter;
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
    @Column(name="coming_soon_indicator", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private Boolean comingSoon;

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

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<SubscriptionFeature> features = new ArrayList<>();

    @Override
    public void onPrePersist() {
       super.onPrePersist();
       features.forEach(feature -> feature.setSubscription(this));
    }

    @Override
    public void onPreUpdate() {
        features.forEach(feature -> feature.setSubscription(this));
    }

    public void upsertFeature(UpdateSubscriptionFeatureRequest upsertFeature) {
        features.stream()
                .filter(feature -> upsertFeature.feature().equals(feature.getFeature()))
                .findFirst()
                .ifPresentOrElse( feature -> {
                            feature.setFeature(upsertFeature.featureName());
                            feature.setFeatureName(upsertFeature.featureName());
                            feature.setDisplay(upsertFeature.display());
                            feature.setConfigMap(upsertFeature.configMap());
                        },
                        () -> features.add(SubscriptionFeature.builder()
                                        .feature(upsertFeature.feature())
                                        .featureName(upsertFeature.featureName())
                                        .display(upsertFeature.display())
                                        .configMap(upsertFeature.configMap())
                                .build()));
    }
}
