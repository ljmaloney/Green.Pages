package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "classified_category")
public class ClassifiedCategory extends Mutable {
  @Column(name = "active", length = 1, nullable = false)
  @Convert(converter = BooleanConverter.class)
  private Boolean active;

  @NotNull
  @Column(name = "name", length = 75, nullable = false)
  private String name;

  @NotNull
  @Column(name = "url_name", length = 75, nullable = false)
  private String urlName;

  @NotNull
  @Column(name = "short_description", length = 100, nullable = false)
  private String shortDescription;

  @Column(name = "description", length = 512)
  private String description;
}
