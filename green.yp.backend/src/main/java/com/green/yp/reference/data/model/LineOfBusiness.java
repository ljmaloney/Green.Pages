package com.green.yp.reference.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "line_of_business", schema = "greenyp")
public class LineOfBusiness extends Mutable {

  @Column(name = "line_of_business", unique = true, nullable = false, length = 50)
  private String lineOfBusinessName;

  @Column(name="url_lob", unique = true, nullable = false, length = 50)
  private String urlLob;

  @Column(name = "created_by_type")
  @Enumerated(EnumType.STRING)
  private LineOfBusinessCreateType createType;

  @Column(name = "create_by_reference")
  private String createByReference;

  @Column(name = "description", length = 1024)
  private String description;

  @Column(name = "enable_distance_radius")
  @Convert(converter = BooleanConverter.class)
  private Boolean enableDistanceRadius;

  @Column(name = "icon_name")
  private String iconName;

  @Column(name = "icon_file_name")
  private String iconFileName;

  @Column(name = "short_description", length = 200)
  private String shortDescription;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    LineOfBusiness that = (LineOfBusiness) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(lineOfBusinessName, that.lineOfBusinessName)
        .append(createType, that.createType)
        .append(createByReference, that.createByReference)
        .append(description, that.description)
        .append(enableDistanceRadius, that.enableDistanceRadius)
        .append(iconName, that.iconName)
        .append(iconFileName, that.iconFileName)
        .append(shortDescription, that.shortDescription)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(lineOfBusinessName)
        .append(createType)
        .append(createByReference)
        .append(description)
        .append(enableDistanceRadius)
        .append(iconName)
        .append(iconFileName)
        .append(shortDescription)
        .toHashCode();
  }
}
