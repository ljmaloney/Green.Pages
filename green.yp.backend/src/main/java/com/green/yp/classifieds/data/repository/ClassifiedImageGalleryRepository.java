package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifiedImageGalleryRepository
    extends JpaRepository<ClassifiedImageGallery, UUID> {
  List<ClassifiedImageGallery> findClassifiedImageGalleriesByClassifiedId(
      @NonNull @NotNull UUID classifiedId);

  void deleteClassifiedImageGalleriesByClassifiedIdAndImageFilename(
      UUID classifiedId, String imageFilename);

  Optional<ClassifiedImageGallery> findFirstByClassifiedId(UUID classifiedId);
}
