package com.green.yp.reference.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode
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
}
