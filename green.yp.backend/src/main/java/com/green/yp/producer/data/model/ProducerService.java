package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.enumeration.ServicePriceUnitsType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
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
}
