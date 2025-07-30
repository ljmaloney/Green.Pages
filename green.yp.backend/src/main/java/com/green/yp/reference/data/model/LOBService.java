package com.green.yp.reference.data.model;

import com.green.yp.api.apitype.enumeration.CreatedByType;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    LOBService that = (LOBService) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(lineOfBusinessId, that.lineOfBusinessId)
        .append(createdByReference, that.createdByReference)
        .append(createdByType, that.createdByType)
        .append(serviceName, that.serviceName)
        .append(serviceDescription, that.serviceDescription)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(lineOfBusinessId)
        .append(createdByReference)
        .append(createdByType)
        .append(serviceName)
        .append(serviceDescription)
        .toHashCode();
  }
}
