package com.green.yp.classifieds.data.model;

import com.green.yp.api.apitype.enumeration.ClassifiedTokenType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "classified_token", schema = "greenyp")
@AllArgsConstructor
public class ClassifiedToken extends Mutable {

  @NotNull
  @Column(name = "classified_id", nullable = false)
  private UUID classifiedId;

  @NotNull
  @Column(name = "token_expiry_date", nullable = false)
  private OffsetDateTime tokenExpiryDate;

  @Size(max = 255)
  @NotNull
  @Column(name = "token_value", nullable = false)
  private String tokenValue;

  @Convert(converter = BooleanConverter.class)
  @Column(name = "token_used")
  private Boolean tokenUsed;

  @Size(max = 15)
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "destination_type", nullable = false, length = 15)
  private ClassifiedTokenType destinationType;

  @Size(max = 150)
  @NotNull
  @Column(name = "destination", nullable = false, length = 150)
  private String destination;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ClassifiedToken that = (ClassifiedToken) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(classifiedId, that.classifiedId)
        .append(tokenExpiryDate, that.tokenExpiryDate)
        .append(tokenValue, that.tokenValue)
        .append(tokenUsed, that.tokenUsed)
        .append(destinationType, that.destinationType)
        .append(destination, that.destination)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(classifiedId)
        .append(tokenExpiryDate)
        .append(tokenValue)
        .append(tokenUsed)
        .append(destinationType)
        .append(destination)
        .toHashCode();
  }
}
