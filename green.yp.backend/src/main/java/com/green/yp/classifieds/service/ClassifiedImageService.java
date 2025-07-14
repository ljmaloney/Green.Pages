package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedImageResponse;
import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import com.green.yp.classifieds.data.repository.ClassifiedImageGalleryRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedImageGalleryMapper;
import com.green.yp.exception.NotFoundException;
import com.green.yp.image.ImageFileService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ClassifiedImageService {

  @Value("${greenyp.image.service.classified.base:classified}")
  private String basePath;

  private final ClassifiedRepository classifedRepository;
  private final ClassifiedImageGalleryRepository imageGalleryRepository;
  private final ClassifiedImageGalleryMapper imageGalleryMapper;
  private final ImageFileService imageFileService;

  public ClassifiedImageService(
      ClassifiedRepository classifedRepository,
      ClassifiedImageGalleryRepository imageGalleryRepository,
      ClassifiedImageGalleryMapper imageGalleryMapper,
      ImageFileService imageFileService) {
    this.classifedRepository = classifedRepository;
    this.imageGalleryRepository = imageGalleryRepository;
    this.imageGalleryMapper = imageGalleryMapper;
    this.imageFileService = imageFileService;
  }

  public List<ClassifiedImageResponse> getGalleryImages(@NonNull @NotNull UUID classifiedId) {

    var images = imageGalleryRepository.findClassifiedImageGalleriesByClassifiedId(classifiedId);
    log.debug("Found {} images in the image gallery for {}", images.size(), classifiedId);
    return imageGalleryMapper.mapToResponse(images);
  }

  public void uploadGalleryImage(
      UUID classifiedId, String imageFilename, String description, MultipartFile file) {
    log.info("Uploading gallery image for classifiedId: {} - {}", classifiedId, imageFilename);
    var classified =
        classifedRepository
            .findById(classifiedId)
            .orElseThrow(() -> new NotFoundException("classified", classifiedId));

    String urlPath = imageFileService.saveImage(classifiedId, basePath, imageFilename, file);

    imageGalleryRepository.saveAndFlush(
        ClassifiedImageGallery.builder()
            .classifiedId(classifiedId)
            .imageFilename(imageFilename)
            .url(urlPath)
            .description(description)
            .build());
  }

  public void deleteGallaryImage(UUID classifiedId, String imageFilename, String requestIP) {
    log.info("Deleting gallery image {} for classifiedId: {} - {}",imageFilename, classifiedId, requestIP);
    classifedRepository
        .findById(classifiedId)
        .orElseThrow(() -> new NotFoundException("producer", classifiedId));
    imageFileService.deleteImage(classifiedId, basePath, imageFilename);
    imageGalleryRepository.deleteClassifiedImageGalleriesByClassifiedIdAndImageFilename(
        classifiedId, imageFilename);
  }

  @Transactional
  public void deleteGalleryImages(UUID classifiedId) {
    log.info("Deleting all gallery images for classifiedId: {}", classifiedId);
    var images = imageGalleryRepository.findClassifiedImageGalleriesByClassifiedId(classifiedId);
    images.forEach(
        image -> {
          imageFileService.deleteImage(classifiedId, basePath, image.getImageFilename());
          imageGalleryRepository.deleteClassifiedImageGalleriesByClassifiedIdAndImageFilename(
              classifiedId, image.getImageFilename());
        });
  }
}
