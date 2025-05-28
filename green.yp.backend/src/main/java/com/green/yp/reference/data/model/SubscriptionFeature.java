package com.green.yp.reference.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscription_feature", schema = "greenyp")
public class SubscriptionFeature extends Mutable {

    @Column(name="start_date")
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(name="end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Column(name="feature_name")
    private String featureName;

    @ManyToOne
    @JoinColumn(name="subscription_id")
    private Subscription subscription;

  @Override
  public void onPrePersist() {
        if ( startDate == null )
            startDate = LocalDate.now();
        if ( endDate == null )
            endDate = LocalDate.of(9999,12,31);
        super.onPrePersist();
    }
}
