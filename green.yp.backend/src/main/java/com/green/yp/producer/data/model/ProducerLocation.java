package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.producer.enumeration.LocationDisplayType;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "producer_location", schema = "greenyp")
public class ProducerLocation extends Mutable {
  @NotNull
  @Column(name = "producer_id", nullable = false)
  private UUID producerId;

  @NotNull
  @Column(name = "location_name", length = 100, nullable = false)
  private String locationName;

  @NotNull
  @Column(name = "location_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ProducerLocationType locationType;

  @NotNull
  @Column(name = "display_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private LocationDisplayType locationDisplayType;

  @Convert(converter = BooleanConverter.class)
  @Column(name = "active", nullable = false)
  private Boolean active;

  @NotNull
  @Column(name = "address_line_1", nullable = false)
  private String addressLine1;

  @Column(name = "address_line_2")
  private String addressLine2;

  @Column(name = "address_line_3")
  private String addressLine3;

  @NotNull
  @Column(name = "city", nullable = false)
  private String city;

  @NotNull
  @Column(name = "state", length = 2, nullable = false)
  private String state;

  @NotNull
  @Column(name = "postal_code", length = 10, nullable = false)
  private String postalCode;

  @Column(name = "latitude", precision = 9, scale = 5)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 9, scale = 5)
  private BigDecimal longitude;

  @Column(name = "website_url", length = 150)
  private String websiteUrl;

  @OneToMany(mappedBy = "producerLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProducerLocationHours> locationHours;

  public boolean isActive() {
    return active != null && active;
  }

  public void addAllHours(List<ProducerLocationHours> locationHours) {
    if (this.locationHours == null) {
      this.locationHours = new ArrayList<>();
    }
    this.locationHours.addAll(locationHours);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerLocation that = (ProducerLocation) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(producerId, that.producerId)
        .append(locationName, that.locationName)
        .append(locationType, that.locationType)
        .append(locationDisplayType, that.locationDisplayType)
        .append(active, that.active)
        .append(addressLine1, that.addressLine1)
        .append(addressLine2, that.addressLine2)
        .append(addressLine3, that.addressLine3)
        .append(city, that.city)
        .append(state, that.state)
        .append(postalCode, that.postalCode)
        .append(latitude, that.latitude)
        .append(longitude, that.longitude)
        .append(websiteUrl, that.websiteUrl)
        .append(locationHours, that.locationHours)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(producerId)
        .append(locationName)
        .append(locationType)
        .append(locationDisplayType)
        .append(active)
        .append(addressLine1)
        .append(addressLine2)
        .append(addressLine3)
        .append(city)
        .append(state)
        .append(postalCode)
        .append(latitude)
        .append(longitude)
        .append(websiteUrl)
        .append(locationHours)
        .toHashCode();
  }
}
