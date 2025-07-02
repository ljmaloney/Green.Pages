package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedImageResponse;
import com.green.yp.api.apitype.common.ResponseApi;
import com.green.yp.classifieds.service.ClassifiedImageService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("classified/images")
@Validated
@Tag(name = "Endpoints for managing images uploaded for a classified ad")
@RestController
public class ClassifiedImageController {

  private final ClassifiedImageService imageService;

  public ClassifiedImageController(ClassifiedImageService imageService) {
    this.imageService = imageService;
  }

  @GetMapping(path = "{classifiedId}/gallery", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ClassifiedImageResponse>> getImages(
      @PathVariable("classifiedId") UUID producerId) {
    return new ResponseApi<>(imageService.getGalleryImages(producerId), null);
  }

  @PostMapping(
      path = "{classifiedId}/gallery",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void uploadGalleryImage(
      @PathVariable("classifiedId") UUID classifiedId,
      @RequestParam("imageFilename") String imageFilename,
      @RequestParam("imageDescription") String imageDescription,
      @RequestParam("file") MultipartFile file) {
    log.info("Uploading gallery image for : {}", classifiedId);
    imageService.uploadGalleryImage(classifiedId, imageFilename, imageDescription, file);
  }

  @DeleteMapping(path = "{classifiedId}/gallery")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGalleryImage(
      @PathVariable("classifiedId") UUID classifiedId,
      @RequestParam(name = "imageFilename", required = true) String imageFilename) {
    log.info("Deleting image {} for {} gallery", imageFilename, classifiedId);
    imageService.deleteGallaryImage(classifiedId, imageFilename, RequestUtil.getRequestIP());
  }
}
