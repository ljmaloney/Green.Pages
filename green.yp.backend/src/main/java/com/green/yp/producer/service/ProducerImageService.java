package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.api.contract.SearchContract;
import com.green.yp.api.contract.SubscriptionContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.image.ImageFileService;
import com.green.yp.producer.data.model.ImageGallery;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.repository.ImageGalleryRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.mapper.ImageGalleryMapper;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.util.RequestUtil;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ProducerImageService {

  private static final String LOGO_FEATURE = "logo";
  private static final String IMAGE_GALLERY_FEATURE = "gallery";
  public static final String FEATURE_NOT_FOUND_MESSAGE =
      "The feature {} does not exist for the producer {}";
  public static final String SUBSCRIPTION_VALIDATION_WARNING =
      "Missing active producer subscription for file upload validation";

  private final ProducerRepository producerRepository;
  private final ImageGalleryRepository imageGalleryRepository;
  private final ImageGalleryMapper imageGalleryMapper;
  private final ImageFileService imageFileService;
  private final SubscriptionContract subscriptionContract;
  private final SearchContract searchContract;

  public ProducerImageService(
          ProducerRepository producerRepository,
          ImageGalleryRepository imageGalleryRepository,
          ImageGalleryMapper imageGalleryMapper,
          ImageFileService imageFileService,
          SubscriptionContract subscriptionContract, SearchContract searchContract) {
    this.producerRepository = producerRepository;
    this.imageGalleryRepository = imageGalleryRepository;
    this.imageGalleryMapper = imageGalleryMapper;
    this.imageFileService = imageFileService;
    this.subscriptionContract = subscriptionContract;
      this.searchContract = searchContract;
  }

  public List<ProducerImageResponse> getGalleryImages(@NonNull @NotNull UUID producerId) {

    var images = imageGalleryRepository.findImageGalleriesByProducerId(producerId);
    log.debug("Found {} images in the image gallery for {}", images.size(), producerId);
    return imageGalleryMapper.mapToResponse(images);
  }

    @Transactional
  public void uploadLogoImage(UUID producerId, String logoFileName, MultipartFile file) {
      if ( RequestUtil.isInValidFileName(logoFileName)) {
          log.warn("Attempting to upload an invalid logoFileName {} by {}", logoFileName, producerId);
          throw new PreconditionFailedException("Invalid filename " + logoFileName);
      }
      Producer producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException("producer", producerId));

    if (!subscriptionHasFeature(producer, LOGO_FEATURE)) {
      log.warn(FEATURE_NOT_FOUND_MESSAGE, LOGO_FEATURE, producerId);
      throw new PreconditionFailedException(
          "Uploading a logo requires the {} feature for producer {}", LOGO_FEATURE, producerId);
    }

    String urlPath = imageFileService.saveLogo(producerId, logoFileName, file);

    producer.setIconLink(urlPath);
    producerRepository.saveAndFlush(producer);
    searchContract.upsertIconLink(producer.getId(), urlPath);
  }

    @Transactional
  public void uploadGalleryImage(
      UUID producerId, String imageFilename, String description, MultipartFile file) {
    if ( RequestUtil.isInValidFileName(imageFilename)) {
        log.warn("Attempting to upload an invalid filename {} by {}", imageFilename, producerId);
        throw new PreconditionFailedException("Invalid filename " + imageFilename);
    }
      var producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException("producer", producerId));

    if (!subscriptionHasFeature(producer, IMAGE_GALLERY_FEATURE)) {
      log.warn(FEATURE_NOT_FOUND_MESSAGE, IMAGE_GALLERY_FEATURE, producerId);
      throw new PreconditionFailedException(
          "Uploading an image requires the {} feature for producer {}",
          IMAGE_GALLERY_FEATURE,
          producerId);
    }

    String urlPath = imageFileService.saveImage(producerId, imageFilename, file);

    imageGalleryRepository.saveAndFlush(
        ImageGallery.builder()
            .producerId(producerId)
            .imageFilename(imageFilename)
            .url(urlPath)
            .description(description)
            .build());
  }

  public ProducerImageResponse getProducerLogo(UUID producerId) {
    var producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException("producer", producerId));
    return new ProducerImageResponse(
        StringUtils.substringAfterLast(producer.getIconLink(), "/"),
        "producer logo",
        producer.getIconLink());
  }

    @Transactional
  public void deleteLogo(UUID producerId, String requestIP) {
    Producer producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException("producer", producerId));

    imageFileService.deleteLogo(producerId);
    producer.setIconLink(null);
    producerRepository.saveAndFlush(producer);
  }

  @Transactional
  public void deleteGalleryImage(@NotNull @NonNull UUID producerId,
                                 @NotNull @NonNull String imageFilename, String requestIP) {
      if ( StringUtils.isBlank(imageFilename)){
          log.warn("No imageFileName specified for deletion for {}", producerId);
          throw new PreconditionFailedException("No imageFileName specified for deletion");
      }
      producerRepository
        .findById(producerId)
        .orElseThrow(() -> new NotFoundException("producer", producerId));
      log.info("Deleting gallery image {} for producer {} from {}", imageFilename,producerId, requestIP);
    imageFileService.deleteImage(producerId, imageFilename);
    imageGalleryRepository.deleteImageGalleryByProducerIdAndImageFilename(
        producerId, imageFilename);
  }

  private boolean subscriptionHasFeature(Producer producer, String featureName) {
    var subscription = getTopSubscription(producer);
    return subscription.features().stream()
        .anyMatch(feature -> feature.feature().equals(featureName));
  }

  private SubscriptionDto getTopSubscription(Producer producer) {
    var today = ChronoLocalDate.from(LocalDateTime.now());

    var subscriptions = subscriptionContract.getAllSubscriptions(SubscriptionType.TOP_LEVEL, true);

    var subscriptionSet =
        subscriptions.stream()
            .map(SubscriptionDto::subscriptionId)
            .collect(Collectors.toUnmodifiableSet());
    log.debug("subscriptionSet {}", subscriptionSet);

    var producerSubscription =
        producer.getSubscriptionList().stream()
            .filter(sub -> {
                log.debug("subscription {} startDate {} endDate {}", sub.getSubscriptionId(), sub.getStartDate(), sub.getEndDate());
                return sub.isSubscriptionActive(today);
            })
            .filter(sub -> {
                log.debug("determine if subscription {} is in set", sub.getSubscriptionId());
                return subscriptionSet.contains(sub.getSubscriptionId());
            })
            .findFirst()
            .orElseThrow(
                () -> {
                  log.warn("The producer {} subscription could not be found or is not active", producer.getId());
                  return new PreconditionFailedException(SUBSCRIPTION_VALIDATION_WARNING);
                });

    return subscriptions.stream()
        .filter(sub -> sub.subscriptionId().equals(producerSubscription.getSubscriptionId()))
        .findFirst()
        .orElseThrow(
            () -> {
              // note this really should not happen ...
              log.warn("Subscription {} could not be found or is not active", producerSubscription.getSubscriptionId());
              return new PreconditionFailedException(SUBSCRIPTION_VALIDATION_WARNING);
            });
  }
}
