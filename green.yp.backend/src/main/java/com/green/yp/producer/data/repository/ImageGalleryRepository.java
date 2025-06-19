package com.green.yp.producer.data.repository;


import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.producer.data.model.ImageGallery;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageGalleryRepository extends JpaRepository<ImageGallery, UUID> {
    List<ImageGallery> findImageGalleriesByProducerId(@NonNull @NotNull UUID producerId);
}
