package com.green.yp.geolocation.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "postal_code_geocode", schema = "greenyp")
public class PostalCodeGeocode {
  @Id
  @Size(max = 10)
  @Column(name = "postal_code", nullable = false, length = 10)
  private String postalCode;

  @Size(max = 100)
  @Column(name = "place_name", length = 100)
  private String placeName;

  @Size(max = 2)
  @Column(name = "state", length = 2)
  private String state;

  @NotNull
  @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
  private BigDecimal latitude;

  @NotNull
  @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
  private BigDecimal longitude;

//    @Column(
//            name = "geo_point",
//            columnDefinition = "POINT",
//            insertable = false,
//            updatable = false)
//    private Point geoPoint;
}
