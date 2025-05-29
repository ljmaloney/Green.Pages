package com.green.yp.producer.data.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Data
@Entity
@EqualsAndHashCode
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
}
