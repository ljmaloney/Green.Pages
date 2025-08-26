package com.green.yp.producer.service;

import static java.lang.Boolean.TRUE;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.common.GeocodeLocation;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.common.ServiceUtils;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import com.green.yp.producer.mapper.ProducerLocationMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerLocationService {

  private static final String PRODUCER_LOCATION = "ProducerLocation";

  final ProducerLocationMapper producerLocationMapper;
  final ProducerLocationRepository locationRepository;
  final ProducerOrchestrationService producerService;
  final ProducerLocationHoursService hoursService;
  private final ProducerGeocodeService geocodingService;

  public ProducerLocationService(
          ProducerLocationMapper producerLocationMapper,
          ProducerLocationRepository locationRepository,
          ProducerOrchestrationService producerService,
          ProducerLocationHoursService hoursService,
          ProducerGeocodeService gecodeService) {
    this.producerLocationMapper = producerLocationMapper;
    this.locationRepository = locationRepository;
    this.producerService = producerService;
    this.hoursService = hoursService;
      this.geocodingService = gecodeService;
  }



  @AuditRequest(
      requestParameter = "createLocationRequest",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.CREATE)
  public ProducerLocationResponse createLocation(
          @Valid CreateLocationRequest createLocationRequest, UUID producerId, String ipAddress) {

    producerService.findActiveProducer(producerId);

    ProducerLocation location = producerLocationMapper.toEntity(createLocationRequest.locationRequest());
    location.setProducerId(producerId);
    location.setActive(true);

    if (location.getLatitude() == null || location.getLongitude() == null) {
      var coordinates = geocodingService.geocodeLocation(location);
      location.setLatitude(coordinates.latitude());
      location.setLongitude(coordinates.longitude());
    }

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
      LocationRequest updateLocationRequest, AuthenticatedUser user, String requestIP) {

    log.info("Updating location {} from {}", updateLocationRequest.locationId(), requestIP);

    ProducerLocation location =
        locationRepository
            .findById(updateLocationRequest.locationId())
            .orElseThrow(
                () -> new NotFoundException(PRODUCER_LOCATION, updateLocationRequest.locationId()));

    boolean addressChanged = addressChanged(location, updateLocationRequest);

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
    location.setWebsiteUrl(updateLocationRequest.websiteUrl());

    if (addressChanged) {
      GeocodeLocation geocodeLocation = geocodingService.geocodeLocation(location);
      location.setLatitude(geocodeLocation.latitude());
      location.setLongitude(geocodeLocation.longitude());
    }
    return producerLocationMapper.fromEntity(locationRepository.saveAndFlush(location));
  }

  private boolean addressChanged(ProducerLocation location, LocationRequest updateLocationRequest) {
    return !StringUtils.equals(location.getAddressLine1(), updateLocationRequest.addressLine1())
        || !StringUtils.equals(
            StringUtils.trimToEmpty(location.getAddressLine2()),
            StringUtils.trimToEmpty(updateLocationRequest.addressLine2()))
        || !StringUtils.equals(location.getCity(), updateLocationRequest.city())
        || !StringUtils.equals(location.getState(), updateLocationRequest.state())
        || !StringUtils.equals(location.getPostalCode(), updateLocationRequest.postalCode());
  }

  ProducerLocation findActiveLocation(@NonNull UUID locationId) {
    ProducerLocation location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new NotFoundException(PRODUCER_LOCATION, locationId));

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
    log.info("Loading location for locationId {} including hours-{}", locationId, includeHours);

    ProducerLocation location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new NotFoundException(PRODUCER_LOCATION, locationId));

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
    if (activeOnly) {
      producerLocations = locationRepository.findActiveLocations(producerId, true);
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
            .orElseThrow(() -> new NotFoundException(PRODUCER_LOCATION, locationId));

    ServiceUtils.patchEntity(patchRequest, location);

    return producerLocationMapper.fromEntity(locationRepository.saveAndFlush(location));
  }

  public void deleteLocation(UUID locationId, String userId, String requestIP) {
    locationRepository
        .findById(locationId)
        .filter(loc -> loc.getLocationType() != ProducerLocationType.HOME_OFFICE_PRIMARY)
            .map( loc -> {
                        loc.setActive(false);
                        locationRepository.save(loc);
                        log.info("Location identified by {} deleted ", locationId);
                        return true;
                })
            .orElseGet( () -> {
                log.info("Location identified by {} already deleted or is Primary", locationId);
                      return false;
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
