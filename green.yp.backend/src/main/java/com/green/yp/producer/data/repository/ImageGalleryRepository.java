package com.green.yp.producer.data.repository;


import com.green.yp.producer.data.model.ImageGallery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageGalleryRepository extends JpaRepository<ImageGallery, UUID> {}
