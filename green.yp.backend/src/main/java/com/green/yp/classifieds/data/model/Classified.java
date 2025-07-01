package com.green.yp.classifieds.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "classified", schema = "greenyp")
public class Classified {
  @Id
  @Size(max = 16)
  @Column(name = "id", nullable = false, length = 16)
  private String id;

  @Size(max = 45)
  @NotNull
  @Column(name = "version", nullable = false, length = 45)
  private String version;

  @NotNull
  @Column(name = "create_date", nullable = false)
  private Instant createDate;

  @NotNull
  @Column(name = "last_update_date", nullable = false)
  private Instant lastUpdateDate;

  @NotNull
  @Column(name = "last_active_date", nullable = false)
  private LocalDate lastActiveDate;

  @ColumnDefault("0")
  @Column(name = "renewal_count")
  private Integer renewalCount;

  @NotNull
  @Column(name = "price", nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Size(max = 50)
  @NotNull
  @Column(name = "per_unit_type", nullable = false, length = 50)
  private String perUnitType;

  @Size(max = 200)
  @NotNull
  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @NotNull
  @Lob
  @Column(name = "description", nullable = false)
  private String description;

  @Size(max = 100)
  @NotNull
  @Column(name = "city", nullable = false, length = 100)
  private String city;

  @Size(max = 12)
  @NotNull
  @Column(name = "postal_code", nullable = false, length = 12)
  private String postalCode;

  @Size(max = 150)
  @NotNull
  @Column(name = "email_address", nullable = false, length = 150)
  private String emailAddress;

  @Size(max = 15)
  @NotNull
  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @NotNull
  @Column(name = "longitude", nullable = false)
  private Double longitude;

  @NotNull
  @Column(name = "latitude", nullable = false)
  private Double latitude;
}
