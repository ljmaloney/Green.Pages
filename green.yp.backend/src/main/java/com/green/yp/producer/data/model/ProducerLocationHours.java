package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.common.enumeration.DayOfWeekType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_location_hours", schema = "greenyp")
public class ProducerLocationHours extends Mutable {
  @NotNull
  @Column(name = "producer_id", nullable = false)
  private UUID producerId;

  @NotNull
  @Column(name = "producer_location_id", nullable = false, insertable = false, updatable = false)
  private UUID producerLocationId;

  @NotNull
  @Column(name = "day_of_week")
  @Enumerated(EnumType.STRING)
  private DayOfWeekType dayOfWeek;

  @NotNull
  @Column(name = "open_time")
  private String openTime;

  @NotNull
  @Column(name = "close_time")
  private String closeTime;

  @ManyToOne
  @JoinColumn(name = "producer_location_id")
  private ProducerLocation producerLocation;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerLocationHours that = (ProducerLocationHours) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(producerId, that.producerId)
        .append(producerLocationId, that.producerLocationId)
        .append(dayOfWeek, that.dayOfWeek)
        .append(openTime, that.openTime)
        .append(closeTime, that.closeTime)
        .append(producerLocation, that.producerLocation)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(producerId)
        .append(producerLocationId)
        .append(dayOfWeek)
        .append(openTime)
        .append(closeTime)
        .append(producerLocation)
        .toHashCode();
  }
}
