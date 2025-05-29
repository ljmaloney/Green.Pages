package com.green.yp.reference.data.model;

import com.green.yp.api.apitype.enumeration.CreatedByType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lob_service", schema = "greenyp")
public class LOBService extends Mutable {
  @Column(name = "line_of_business_id")
  private UUID lineOfBusinessId;

  @NotNull
  @Column(name = "created_by_reference", nullable = false, length = 50)
  private String createdByReference;

  @NotNull
  @Column(name = "created_by_type", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private CreatedByType createdByType;

  @NotNull
  @Column(name = "service_name", nullable = false, length = 50)
  private String serviceName;

  @Column(name = "service_description", length = 512)
  private String serviceDescription;
}
