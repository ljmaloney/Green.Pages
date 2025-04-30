package com.green.yp.producer.data.model;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProducerLineOfBusinessId implements Serializable {

  private UUID producerId;

  private UUID lineOfBusinessId;
}
