package com.green.yp.producer.data.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProducerLineOfBusinessId.class)
@Table(name = "producer_line_of_business", schema = "greenyp")
public class ProducerLineOfBusiness {

  @Id
  @Column(name = "producer_id")
  private UUID producerId;

  @Id
  @Column(name = "line_of_business_id", length = 50)
  private UUID lineOfBusinessId;

  @Column(name = "primary_lob")
  private Boolean primaryLob;

  @ManyToOne
  @JoinColumn(name = "producer_id", insertable = false, updatable = false)
  private Producer producer;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerLineOfBusiness that = (ProducerLineOfBusiness) o;

    return new EqualsBuilder()
        .append(producerId, that.producerId)
        .append(lineOfBusinessId, that.lineOfBusinessId)
        .append(primaryLob, that.primaryLob)
        .append(producer, that.producer)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(producerId)
        .append(lineOfBusinessId)
        .append(primaryLob)
        .append(producer)
        .toHashCode();
  }
}
