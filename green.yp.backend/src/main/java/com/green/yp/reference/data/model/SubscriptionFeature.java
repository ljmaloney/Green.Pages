package com.green.yp.reference.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.converter.JsonMapConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Map;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
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

  @Column(name = "feature")
  private String feature;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Column(name = "display")
  @Convert(converter = BooleanConverter.class)
  private Boolean display;

  @Column(name = "feature_name")
  private String featureName;

  @Column(name = "config")
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
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    SubscriptionFeature that = (SubscriptionFeature) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(startDate, that.startDate)
        .append(endDate, that.endDate)
        .append(feature, that.feature)
        .append(sortOrder, that.sortOrder)
        .append(display, that.display)
        .append(featureName, that.featureName)
        .append(configMap, that.configMap)
        .append(subscription, that.subscription)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(startDate)
        .append(endDate)
        .append(feature)
        .append(sortOrder)
        .append(display)
        .append(featureName)
        .append(configMap)
        .append(subscription)
        .toHashCode();
  }

  @Override
  public String toString() {
    return "SubscriptionFeature ["
        + "id:"
        + getId().toString()
        + ", subscriptionId:"
        + subscription.getId()
        + ", startDate:"
        + startDate
        + ", endDate:"
        + endDate
        + ", featureName:"
        + featureName
        + "]";
  }
}
