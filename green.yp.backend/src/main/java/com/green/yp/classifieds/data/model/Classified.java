package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "classified", schema = "greenyp")
public class Classified extends Mutable {

  @Column(name = "active_date", nullable = true)
  @Temporal(TemporalType.DATE)
  private LocalDate activeDate;

  @Column(name = "last_active_date", nullable = true)
  @Temporal(TemporalType.DATE)
  private LocalDate lastActiveDate;

  @Column(name = "classified_ad_type")
  private UUID adTypeId;

  @Column(name = "classified_category_id")
  private UUID categoryId;

  @Column(name = "classified_customer_id")
  private UUID classifiedCustomerId;

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

  @Size(max = 100)
  @NotNull
  @Column(name = "state", nullable = false, length = 100)
  private String state;

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
  private BigDecimal longitude;

  @NotNull
  @Column(name = "latitude", nullable = false)
  private BigDecimal latitude;
}
