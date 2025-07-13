package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassifiedImageGalleryRepository
    extends JpaRepository<ClassifiedImageGallery, UUID> {
  List<ClassifiedImageGallery> findClassifiedImageGalleriesByClassifiedId(
      @NonNull @NotNull UUID classifiedId);

  void deleteClassifiedImageGalleriesByClassifiedIdAndImageFilename(
      UUID classifiedId, String imageFilename);

  Optional<ClassifiedImageGallery> findFirstByClassifiedId(UUID classifiedId);

  @Query("""
            SELECT image
            FROM ClassifiedImageGallery image
            WHERE image.classifiedId IN :classifiedIds
        """)
  List<ClassifiedImageGallery> findImagesByClassifiedIds(@Param("classifiedIds") List<UUID> classifiedIds);
}
