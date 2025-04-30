package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.LocationHoursRequest;
import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.model.ProducerLocationHours;
import com.green.yp.producer.data.repository.ProducerLocationHoursRepository;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import com.green.yp.producer.mapper.ProducerLocationHoursMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerLocationHoursService {

  final ProducerLocationHoursMapper locationHoursMapper;

  final ProducerLocationHoursRepository locationHoursRepository;

  private final ProducerLocationRepository locationRepository;

  public ProducerLocationHoursService(
      ProducerLocationHoursMapper locationHoursMapper,
      ProducerLocationHoursRepository locationHoursRepository,
      ProducerLocationRepository locationRepository) {
    this.locationHoursMapper = locationHoursMapper;
    this.locationHoursRepository = locationHoursRepository;
    this.locationRepository = locationRepository;
  }

  public List<LocationHoursResponse> findLocationHours(
      UUID locationId, Object o, String requestIP) {
    return locationHoursMapper.fromEntity(
        locationHoursRepository.findAllByProducerLocationId(locationId));
  }

  @AuditRequest(
      requestParameter = "request",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.CREATE)
  public LocationHoursResponse createLocationHours(
      LocationHoursRequest request, String userId, String requestIP) {
    ProducerLocation location =
        locationRepository
            .findById(request.locationId())
            .orElseThrow(() -> new NotFoundException("ProducerLocation", request.locationId()));

    location.getLocationHours().stream()
        .filter(hours -> hours.getDayOfWeek() == request.dayOfWeek())
        .findFirst()
        .ifPresent(
            hours -> {
              throw new PreconditionFailedException(
                  "Location hours already created for %s and location %s",
                  request.dayOfWeek(), request.locationId());
            });

    ProducerLocationHours savedHours =
        locationHoursRepository.saveAndFlush(
            ProducerLocationHours.builder()
                .producerId(location.getProducerId())
                .producerLocationId(location.getId())
                .dayOfWeek(request.dayOfWeek())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .producerLocation(location)
                .build());

    return locationHoursMapper.fromEntity(savedHours);
  }

  @AuditRequest(
      requestParameter = "hoursRequest",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.UPDATE)
  public LocationHoursResponse updateLocationHours(
      LocationHoursRequest hoursRequest, String userId, String requestIP) {
    log.info(
        "Updating current location hours for location {} and {}",
        hoursRequest.locationId(),
        hoursRequest.dayOfWeek());

    ProducerLocationHours hours =
        locationHoursRepository
            .findLocationHours(hoursRequest.locationId(), hoursRequest.dayOfWeek())
            .orElseGet(
                () -> {
                  ProducerLocation location =
                      locationRepository
                          .findById(hoursRequest.locationId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "ProducerLocation", hoursRequest.locationId()));

                  return ProducerLocationHours.builder()
                      .producerId(location.getProducerId())
                      .producerLocationId(location.getId())
                      .dayOfWeek(hoursRequest.dayOfWeek())
                      .openTime(hoursRequest.openTime())
                      .closeTime(hoursRequest.closeTime())
                      .producerLocation(location)
                      .build();
                });
    if (hours.getVersion() != null) {
      try {
        PropertyUtils.copyProperties(hours, hoursRequest);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        log.error("Unexpected error updating location hours - {}", e.getMessage(), e);
        throw new BusinessException(
            "Unexpected error updating location hours - %s", e, e.getMessage());
      }
    }

    return locationHoursMapper.fromEntity(locationHoursRepository.saveAndFlush(hours));
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public void deleteLocationHours(UUID locationHoursId, String userId, String requestIP) {
    log.info("Delete locationHoursId {}", locationHoursId);
    locationHoursRepository.deleteById(locationHoursId);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public void deleteAllHours(UUID locationId, String userId, String requestIP) {
    log.info("Delete all location hours for {}", locationId);
    locationHoursRepository.deleteAll(locationId);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  @AuditRequest(
      requestParameter = "producerIds",
      objectType = AuditObjectType.PRODUCER_LOCATION_HOURS,
      actionType = AuditActionType.DELETE)
  public void deleteProducers(List<UUID> producerIds) {
    log.info("Delete all hours for producers {}", producerIds);
    locationHoursRepository.deleteAllByProducers(producerIds);
  }
}
