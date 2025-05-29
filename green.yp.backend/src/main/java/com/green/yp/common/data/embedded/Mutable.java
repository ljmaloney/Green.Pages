package com.green.yp.common.data.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class Mutable extends Versioned {

  @Column(length = 16)
  @Id
  private UUID id = UUID.randomUUID();
}
