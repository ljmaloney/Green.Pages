package com.green.yp.reference.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "botanical_group", schema = "greenyp")
public class BotanicalGroup {
  @NonNull
  @NotNull
  @Id
  @Column(name = "name")
  private String name;

  @Column(name = "create_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime createDate;

  @Column(name = "genus")
  private String genus;

  @Column(name = "species")
  private String species;

  @Column(name = "sub_species")
  private String subSpecies;

  @Column(name = "description")
  private String description;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    BotanicalGroup that = (BotanicalGroup) o;

    return new EqualsBuilder()
        .append(name, that.name)
        .append(createDate, that.createDate)
        .append(genus, that.genus)
        .append(species, that.species)
        .append(subSpecies, that.subSpecies)
        .append(description, that.description)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(createDate)
        .append(genus)
        .append(species)
        .append(subSpecies)
        .append(description)
        .toHashCode();
  }
}
