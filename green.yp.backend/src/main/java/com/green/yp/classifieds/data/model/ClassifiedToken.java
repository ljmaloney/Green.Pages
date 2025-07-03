package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "classified_token", schema = "greenyp")
public class ClassifiedToken extends Mutable {

  @NotNull
  @Column(name = "classified_id", nullable = false)
  private UUID classifiedId;

  @NotNull
  @Column(name = "token_expiry_date", nullable = false)
  private OffsetDateTime tokenExpiryDate;

  @Size(max = 20)
  @NotNull
  @Column(name = "token_value", nullable = false, length = 20)
  private String tokenValue;

  @ColumnDefault("'N'")
  @Convert(converter = BooleanConverter.class)
  @Column(name = "token_used")
  private Boolean tokenUsed;

  @Size(max = 15)
  @NotNull
  @Column(name = "destination_type", nullable = false, length = 15)
  private String destinationType;

  @Size(max = 150)
  @NotNull
  @Column(name = "destination", nullable = false, length = 150)
  private String destination;
}
