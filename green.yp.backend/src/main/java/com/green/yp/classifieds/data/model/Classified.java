package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

  @Column(name = "renewal_count")
  private Integer renewalCount;

  @NotNull
  @Column(name = "price", nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Size(max = 50)
  @NotNull
  @Column(name = "per_unit_type", nullable = false, length = 50)
  private String perUnitType;

  @Column(name="id_token")
  private String idToken;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Classified that = (Classified) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(activeDate, that.activeDate).append(lastActiveDate, that.lastActiveDate).append(adTypeId, that.adTypeId).append(categoryId, that.categoryId).append(classifiedCustomerId, that.classifiedCustomerId).append(renewalCount, that.renewalCount).append(price, that.price).append(perUnitType, that.perUnitType).append(idToken, that.idToken).append(title, that.title).append(description, that.description).append(city, that.city).append(state, that.state).append(postalCode, that.postalCode).append(emailAddress, that.emailAddress).append(phoneNumber, that.phoneNumber).append(longitude, that.longitude).append(latitude, that.latitude).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(activeDate).append(lastActiveDate).append(adTypeId).append(categoryId).append(classifiedCustomerId).append(renewalCount).append(price).append(perUnitType).append(idToken).append(title).append(description).append(city).append(state).append(postalCode).append(emailAddress).append(phoneNumber).append(longitude).append(latitude).toHashCode();
  }
}
