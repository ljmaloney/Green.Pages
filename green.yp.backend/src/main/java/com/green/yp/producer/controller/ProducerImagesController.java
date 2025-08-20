package com.green.yp.producer.controller;

import com.green.yp.api.apitype.common.ResponseApi;
import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.producer.service.ProducerImageService;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("producer")
@Validated
@Tag(name = "Endpoints for managing images uploaded for a producer / subscriber")
@RestController
public class ProducerImagesController {

  private final ProducerImageService imageService;

  public ProducerImagesController(ProducerImageService imageService) {
    this.imageService = imageService;
  }

  @GetMapping(path = "{producerId}/gallery", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerImageResponse>> getImages(
      @PathVariable("producerId") UUID producerId) {
    return new ResponseApi<>(imageService.getGalleryImages(producerId), null);
  }

  @GetMapping(path = "{producerId}/logo", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerImageResponse> getLogo(@PathVariable("producerId") UUID producerId) {
    return new ResponseApi<>(imageService.getProducerLogo(producerId), null);
  }

  @IsAnyAuthenticatedUser
  @PostMapping(
      path = "{producerId}/logo",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void uploadProducerLogo(
      @PathVariable("producerId") UUID producerId,
      @RequestParam("file") MultipartFile file,
      @RequestParam("logoFilename") String logoFileName) {
    log.info("Uploading logo for : {}", producerId);
    imageService.uploadLogoImage(producerId, logoFileName, file);
  }

  @IsAnyAuthenticatedUser
  @PostMapping(
      path = "{producerId}/gallery",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void uploadGalleryImage(
      @PathVariable("producerId") UUID producerId,
      @RequestParam("imageFilename") String imageFilename,
      @RequestParam("imageDescription") String imageDescription,
      @RequestParam("file") MultipartFile file) {
    log.info("Uploading gallery image for : {}", producerId);
    imageService.uploadGalleryImage(producerId, imageFilename, imageDescription, file);
  }

  @IsAnyAuthenticatedUser
  @DeleteMapping(path = "{producerId}/logo")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteLogo(@PathVariable("producerId") UUID producerId, HttpServletRequest request) {
    log.info("Deleting logo for producer {}", producerId);
    imageService.deleteLogo(producerId, RequestUtil.getRequestIP(request));
  }

  @IsAnyAuthenticatedUser
  @DeleteMapping(path = "{producerId}/gallery")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGalleryImage(
      @PathVariable("producerId") UUID producerId,
      @RequestParam(name = "imageFilename") String imageFilename,
      HttpServletRequest request) {
    log.info("Deleting image {} for {} gallery", imageFilename, producerId);
    imageService.deleteGalleryImage(producerId, imageFilename, RequestUtil.getRequestIP(request));
  }
}
