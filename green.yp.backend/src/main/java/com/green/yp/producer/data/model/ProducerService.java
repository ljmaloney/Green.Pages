package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.enumeration.ServicePriceUnitsType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "producer_service", schema = "greenyp")
public class ProducerService extends Mutable {
  @NotNull
  @Column(name = "producer_id", nullable = false)
  private UUID producerId;

  @NotNull
  @Column(name = "producer_location_id", nullable = false)
  private UUID producerLocationId;

  @Column(name = "discontinued")
  @Convert(converter = BooleanConverter.class)
  private Boolean discontinued;

  @Column(name = "discontinue_date")
  @Temporal(TemporalType.DATE)
  private LocalDate discontinueDate;

  @Column(name = "min_service_price")
  private BigDecimal minServicePrice;

  @Column(name = "max_service_price")
  private BigDecimal maxServicePrice;

  @Column(name = "price_units_type")
  @Enumerated(EnumType.STRING)
  private ServicePriceUnitsType priceUnitsType;

  @NotNull
  @Column(name = "service_short_description", nullable = false)
  private String shortDescription;

  @Lob
  @Column(name = "service_description")
  private String description;

  @Lob
  @Column(name = "service_terms")
  private String serviceTerms;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerService service = (ProducerService) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(producerId, service.producerId)
        .append(producerLocationId, service.producerLocationId)
        .append(discontinued, service.discontinued)
        .append(discontinueDate, service.discontinueDate)
        .append(minServicePrice, service.minServicePrice)
        .append(maxServicePrice, service.maxServicePrice)
        .append(priceUnitsType, service.priceUnitsType)
        .append(shortDescription, service.shortDescription)
        .append(description, service.description)
        .append(serviceTerms, service.serviceTerms)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(producerId)
        .append(producerLocationId)
        .append(discontinued)
        .append(discontinueDate)
        .append(minServicePrice)
        .append(maxServicePrice)
        .append(priceUnitsType)
        .append(shortDescription)
        .append(description)
        .append(serviceTerms)
        .toHashCode();
  }
}
