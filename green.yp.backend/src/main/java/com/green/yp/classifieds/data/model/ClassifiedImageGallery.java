package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "classified_image_gallery", schema = "greenyp")
public class ClassifiedImageGallery extends Mutable {

  @Column(name = "image_file_name", length = 75)
  @NotNull
  @NonNull
  private String imageFilename;

  @Column(name = "description", length = 512)
  private String description;

  @Column(name = "image_url_link", length = 256)
  @NotNull
  @NonNull
  private String url;

  @Column(name = "classified_id")
  @NotNull
  @NonNull
  private UUID classifiedId;
}
