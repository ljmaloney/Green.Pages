package com.green.yp.producer.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "producer_image_gallery", schema = "greenyp")
public class ImageGallery extends Mutable {

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

  @Column(name = "producer_id")
  @NotNull
  @NonNull
  private UUID producerId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ImageGallery that = (ImageGallery) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(imageFilename, that.imageFilename)
        .append(description, that.description)
        .append(url, that.url)
        .append(producerId, that.producerId)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(imageFilename)
        .append(description)
        .append(url)
        .append(producerId)
        .toHashCode();
  }
}
