package com.green.yp.classifieds.data.model;

import com.green.yp.classifieds.data.converter.JsonFeatureConvertor;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "classified_ad_type", schema = "greenyp")
@AttributeOverrides({
  @AttributeOverride(name = "createDate", column = @Column(name = "create_date", nullable = false)),
  @AttributeOverride(
      name = "lastUpdateDate",
      column = @Column(name = "last_update_date", nullable = false))
})
public class ClassifiedAdType extends Mutable {

  @NotNull
  @Convert(converter = BooleanConverter.class)
  @Column(name = "active", nullable = false)
  private Boolean active;

  @Size(max = 75)
  @NotNull
  @Column(name = "ad_type_name", nullable = false, length = 75)
  private String adTypeName;

  @NotNull
  @Column(name = "monthly_price", nullable = false, precision = 5, scale = 2)
  private BigDecimal monthlyPrice;

  @Column(name = "three_month_price", precision = 5, scale = 2)
  private BigDecimal threeMonthPrice;

  @Column(name = "features")
  @Convert(converter = JsonFeatureConvertor.class)
  private ClassifiedAdFeature features;
}
