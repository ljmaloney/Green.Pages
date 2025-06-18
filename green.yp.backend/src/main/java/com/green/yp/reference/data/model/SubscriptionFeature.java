package com.green.yp.reference.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.converter.JsonMapConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscription_feature", schema = "greenyp")
public class SubscriptionFeature extends Mutable {

  @Column(name = "start_date")
  @Temporal(TemporalType.DATE)
  private LocalDate startDate;

  @Column(name = "end_date")
  @Temporal(TemporalType.DATE)
  private LocalDate endDate;

  @Column(name="feature")
  private String feature;

  @Column(name="sort_order")
  private Integer sortOrder;

  @Column(name="display")
  @Convert(converter = BooleanConverter.class)
  private Boolean display;

  @Column(name = "feature_name")
  private String featureName;

  @Column(name="config")
  @Convert(converter = JsonMapConverter.class)
  private Map<String, Object> configMap;

  @ManyToOne
  @JoinColumn(name = "subscription_id")
  private Subscription subscription;

  @Override
  public void onPrePersist() {
    if (startDate == null) startDate = LocalDate.now();
    if (endDate == null) endDate = LocalDate.of(9999, 12, 31);
    super.onPrePersist();
  }

  @Override
  public String toString() {
    return "SubscriptionFeature [" +
           "id:" +
           getId().toString() +
           ", subscriptionId:" +
           subscription.getId() +
           ", startDate:" +
           startDate +
           ", endDate:" +
           endDate +
           ", featureName:" +
           featureName +
           "]";
  }
}
