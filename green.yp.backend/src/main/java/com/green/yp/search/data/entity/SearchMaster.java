package com.green.yp.search.data.entity;

import com.green.yp.api.apitype.enumeration.SearchRecordType;
import com.green.yp.common.data.converter.BooleanConverter;
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
@NoArgsConstructor
@Entity
@Table(name = "search_master", schema = "greenyp")
public class SearchMaster extends Mutable {

  @Size(max = 16)
  @Column(name = "extern_id", length = 16)
  private UUID externId;

  @Size(max = 16)
  @Column(name = "producer_id", length = 16)
  private UUID producerId;

  @Size(max = 16)
  @Column(name = "location_id", length = 16)
  private UUID locationId;

  @Size(max = 16)
  @Column(name = "category_ref", length = 16)
  private UUID categoryRef;

  @Column(name = "category_name", length = 150)
  private String categoryName;

  @Enumerated(EnumType.STRING)
  @Column(name = "record_type", length = 50)
  private SearchRecordType recordType;

  @Convert(converter = BooleanConverter.class)
  @Column(name = "active", nullable = false)
  private Boolean active;

  @Temporal(TemporalType.DATE)
  @Column(name = "last_active_date")
  private LocalDate lastActiveDate;

  @Lob
  @Column(name = "keywords")
  private String keywords;

  @Size(max = 256)
  @Column(name = "title", length = 256)
  private String title;

  @Size(max = 256)
  @Column(name = "business_name", length = 256)
  private String businessName;

  @Size(max = 256)
  @Column(name = "business_url", length = 256)
  private String businessUrl;

  @Size(max = 256)
  @Column(name = "business_icon_url", length = 256)
  private String businessIconUrl;

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  private String imageUrl;

  @Size(max = 100)
  @Column(name = "address_line_1", length = 100)
  private String addressLine1;

  @Size(max = 100)
  @Column(name = "address_line_2", length = 100)
  private String addressLine2;

  @Size(max = 100)
  @NotNull
  @Column(name = "city", nullable = false, length = 100)
  private String city;

  @Size(max = 2)
  @NotNull
  @Column(name = "state", nullable = false, length = 2)
  private String state;

  @Size(max = 12)
  @NotNull
  @Column(name = "postal_code", nullable = false, length = 12)
  private String postalCode;

  @Size(max = 150)
  @Column(name = "email_address", nullable = false, length = 150)
  private String emailAddress;

  @Size(max = 15)
  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @Column(name = "min_price", precision = 10, scale = 2)
  private BigDecimal minPrice;

  @Column(name = "max_price", precision = 10, scale = 2)
  private BigDecimal maxPrice;

  @Size(max = 50)
  @Column(name = "price_units_type", length = 50)
  private String priceUnitsType;

  @NotNull
  @Column(name = "longitude", nullable = false)
  private BigDecimal longitude;

  @NotNull
  @Column(name = "latitude", nullable = false)
  private BigDecimal latitude;

  @Lob
  @Column(name = "description")
  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    SearchMaster that = (SearchMaster) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(externId, that.externId)
        .append(producerId, that.producerId)
        .append(locationId, that.locationId)
        .append(categoryRef, that.categoryRef)
        .append(categoryName, that.categoryName)
        .append(recordType, that.recordType)
        .append(active, that.active)
        .append(lastActiveDate, that.lastActiveDate)
        .append(keywords, that.keywords)
        .append(title, that.title)
        .append(businessName, that.businessName)
        .append(businessUrl, that.businessUrl)
        .append(businessIconUrl, that.businessIconUrl)
        .append(imageUrl, that.imageUrl)
        .append(addressLine1, that.addressLine1)
        .append(addressLine2, that.addressLine2)
        .append(city, that.city)
        .append(state, that.state)
        .append(postalCode, that.postalCode)
        .append(emailAddress, that.emailAddress)
        .append(phoneNumber, that.phoneNumber)
        .append(minPrice, that.minPrice)
        .append(maxPrice, that.maxPrice)
        .append(priceUnitsType, that.priceUnitsType)
        .append(longitude, that.longitude)
        .append(latitude, that.latitude)
        .append(description, that.description)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(externId)
        .append(producerId)
        .append(locationId)
        .append(categoryRef)
        .append(categoryName)
        .append(recordType)
        .append(active)
        .append(lastActiveDate)
        .append(keywords)
        .append(title)
        .append(businessName)
        .append(businessUrl)
        .append(businessIconUrl)
        .append(imageUrl)
        .append(addressLine1)
        .append(addressLine2)
        .append(city)
        .append(state)
        .append(postalCode)
        .append(emailAddress)
        .append(phoneNumber)
        .append(minPrice)
        .append(maxPrice)
        .append(priceUnitsType)
        .append(longitude)
        .append(latitude)
        .append(description)
        .toHashCode();
  }
}
