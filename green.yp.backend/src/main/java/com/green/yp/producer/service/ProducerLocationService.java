package com.green.yp.producer.service;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.common.ServiceUtils;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import com.green.yp.producer.mapper.ProducerLocationMapper;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerLocationService {

  final ProducerLocationMapper producerLocationMapper;

  final ProducerLocationRepository locationRepository;

  final ProducerOrchestrationService producerService;

  final ProducerLocationHoursService hoursService;

  public ProducerLocationService(
      ProducerLocationMapper producerLocationMapper,
      ProducerLocationRepository locationRepository,
      ProducerOrchestrationService producerService,
      ProducerLocationHoursService hoursService) {
    this.producerLocationMapper = producerLocationMapper;
    this.locationRepository = locationRepository;
    this.producerService = producerService;
    this.hoursService = hoursService;
  }

  @AuditRequest(
      requestParameter = "createLocationRequest",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.CREATE)
  public ProducerLocationResponse createLocation(
      LocationRequest createLocationRequest, UUID producerId, String ipAddress) {

    Producer producer = producerService.findActiveProducer(producerId);

    ProducerLocation location = producerLocationMapper.toEntity(createLocationRequest);
    location.setProducerId(producerId);
    location.setActive(true);

    // TODO: lookup lat-long

    ProducerLocation savedLocation = locationRepository.saveAndFlush(location);

    log.info(
        "New producer location {} named {} created for {}",
        savedLocation.getId(),
        savedLocation.getLocationName(),
        savedLocation.getProducerId());

    return producerLocationMapper.fromEntity(savedLocation);
  }

  @AuditRequest(
      requestParameter = "updateLocationRequest",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.UPDATE)
  public ProducerLocationResponse updateLocation(
      LocationRequest updateLocationRequest, String userId, String requestIP) {

    log.info("Updating location {} from {}", updateLocationRequest.locationId(), requestIP);

    ProducerLocation location =
        locationRepository
            .findById(updateLocationRequest.locationId())
            .orElseThrow(
                () ->
                    new NotFoundException("ProducerLocation", updateLocationRequest.locationId()));

    location.setLocationName(updateLocationRequest.locationName());
    location.setLocationType(updateLocationRequest.locationType());
    location.setLocationDisplayType(updateLocationRequest.locationDisplayType());
    location.setActive(updateLocationRequest.active());
    location.setAddressLine1(updateLocationRequest.addressLine1());
    location.setAddressLine2(updateLocationRequest.addressLine2());
    location.setAddressLine3(updateLocationRequest.addressLine3());
    location.setCity(updateLocationRequest.city());
    location.setState(updateLocationRequest.state());
    location.setPostalCode(updateLocationRequest.postalCode());
    location.setLatitude(updateLocationRequest.latitude());
    location.setLongitude(updateLocationRequest.longitude());
    location.setWebsiteUrl(updateLocationRequest.websiteUrl());

    return producerLocationMapper.fromEntity(locationRepository.saveAndFlush(location));
  }

  ProducerLocation findActiveLocation(@NonNull UUID locationId) {
    ProducerLocation location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new NotFoundException("ProducerLocation", locationId));

    if (!location.isActive()) {
      log.error("Producer location identified by {} is not active", locationId);
      throw new PreconditionFailedException(
          "Producer location identified by {} is not active", locationId);
    }
    return location;
  }

  public ProducerLocationResponse findPrimaryLocation(@NotNull UUID producerId) {
    log.info("Loading primary location for producer account - {}", producerId);
    return locationRepository
        .findLocation(producerId, TRUE, ProducerLocationType.HOME_OFFICE_PRIMARY)
        .map(producerLocationMapper::fromEntity)
        .orElseThrow(
            () -> {
              log.info("Primary location was not found or is not active for {}", producerId);
              return new NotFoundException(
                  String.format("Primary location not found for %s", producerId));
            });
  }

  public ProducerLocationResponse findLocation(
      @NonNull @NotNull UUID locationId, @NotNull @NonNull Boolean includeHours) {
    log.info(
        "Loading location for locationId {} and producerId {} including hours-{}",
        locationId,
        includeHours);

    ProducerLocation location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new NotFoundException("ProducerLocation", locationId));

    if (includeHours) {
      return producerLocationMapper.fromEntity(location);
    }
    location.setLocationHours(null);

    return producerLocationMapper.fromEntity(location);
  }

  public List<ProducerLocationResponse> findAllLocations(
      @NonNull @NotNull UUID producerId,
      @NotNull @NonNull Boolean activeOnly,
      @NonNull @NotNull Boolean includeHours) {
    log.info("Loading all locations for {}, includeHours:{}", producerId, includeHours);

    List<ProducerLocation> producerLocations;
    if (activeOnly.booleanValue()) {
      producerLocations = locationRepository.findActiveLocations(producerId, activeOnly);
    } else {
      producerLocations = locationRepository.findProducerLocationsByProducerId(producerId);
    }

    if (!includeHours) {
      producerLocations.forEach(location -> location.setLocationHours(null));
    }
    return producerLocationMapper.fromEntity(producerLocations);
  }

  public ProducerLocationResponse patchLocation(
      PatchRequest patchRequest, UUID locationId, String userId, String requestIP) {
    ProducerLocation location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new NotFoundException("ProducerLocation", locationId));

    ServiceUtils.patchEntity(patchRequest, location);

    return producerLocationMapper.fromEntity(locationRepository.saveAndFlush(location));
  }

  public void deleteLocation(UUID locationId, String userId, String requestIP) {
    locationRepository
        .findById(locationId)
        .filter(loc -> loc.getLocationType() != ProducerLocationType.HOME_OFFICE_PRIMARY)
        .ifPresentOrElse(
            loc -> {
              loc.setActive(false);
              locationRepository.save(loc);
              log.info("Location identified by {} deleted ", locationId);
            },
            () -> {
              log.info("Location identified by {} already deleted or is Primary", locationId);
            });
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  @AuditRequest(
      requestParameter = "producerIds",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.DELETE_LOCATION)
  public void deleteLocations(@NonNull @NotNull List<UUID> producerIds) {
    log.info("Delete all locations for producers - {}", producerIds);
    hoursService.deleteProducers(producerIds);

    locationRepository.deleteLocations(producerIds);
  }
}
