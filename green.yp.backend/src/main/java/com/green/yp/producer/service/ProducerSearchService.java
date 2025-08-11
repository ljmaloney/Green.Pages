package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.api.apitype.PageableResponse;
import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.api.contract.LineOfBusinessContract;
import com.green.yp.api.contract.SearchContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.record.ProducerLocationDistanceProjection;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.green.yp.reference.dto.LineOfBusinessDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@Validated
public class ProducerSearchService {

  private static final double MILES_IN_METERS = 1609.34d;
  private final ProducerSearchRepository searchRepository;
  private final ProducerLocationRepository locationRepository;
  private final GeocodingService geocodingService;
  private final ProducerSearchMapper producerSearchMapper;
  private final SearchContract searchContract;
  private final LineOfBusinessContract lobContract;

  public ProducerSearchService(
          ProducerSearchRepository searchRepository,
          ProducerLocationRepository locationRepository,
          @Qualifier("defaultGeocodeServiceImpl") GeocodingService geocodingService,
          ProducerSearchMapper producerSearchMapper,
          SearchContract searchContract, LineOfBusinessContract lobContract) {
    this.searchRepository = searchRepository;
      this.locationRepository = locationRepository;
      this.geocodingService = geocodingService;
    this.producerSearchMapper = producerSearchMapper;
      this.searchContract = searchContract;
      this.lobContract = lobContract;
  }

  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  public PageableResponse<ProducerSearchResponse> search(
      String zipCode, Integer distance, Pageable pageable, UUID categoryId) {
    log.info("Searching for producers near zipCode: {}, within {} miles", zipCode, distance);

    var coordinates = geocodingService.getCoordinates(zipCode);
    var wktPoint = String.format("POINT(%f %f)", coordinates.latitude(), coordinates.longitude());

    BigDecimal distanceMeters = BigDecimal.valueOf(distance).multiply(BigDecimal.valueOf(MILES_IN_METERS));

    var searchLocations =
              searchRepository.findProducersWithinDistance(
                      wktPoint, distanceMeters, categoryId, pageable);

    List<UUID> locationIds =
        searchLocations.get().map(ProducerLocationDistanceProjection::getLocationId).toList();

    var searchResults =
        searchRepository.findProducers(
            locationIds,
            coordinates.latitude().doubleValue(),
            coordinates.longitude().doubleValue());

    log.info(
        "Found {} producers near zipCode: {}, within {} miles",
        searchResults.size(),
        zipCode,
        distance);

    return new PageableResponse<>(
        producerSearchMapper.toResponse(searchResults),
        (int) searchLocations.getTotalElements(),
        searchLocations.getNumber(),
        searchLocations.getTotalPages());
  }

    @Async("threadPoolSearchTaskExecutor")
    public void activateProducer(UUID producerId) {
        log.info("Activating producer search master record for  producerId: {}", producerId);
        log.info("Loading primary location for producer account - {}", producerId);
        locationRepository
                .findLocation(producerId, TRUE, ProducerLocationType.HOME_OFFICE_PRIMARY)
                .ifPresentOrElse( loc -> {
                    createLocationSearchRecords(loc.getId());
                }, () -> {
                    log.info("Primary location was not found or is not active for {}", producerId);
                    throw new NotFoundException(String.format("Primary location not found for %s", producerId));
                });
    }

    @Async("threadPoolSearchTaskExecutor")
    public void createLocationSearchRecords(@NotNull @NonNull UUID locationId) {
      log.info("Creating search master record for  location: {}", locationId);
        List<SearchMasterRequest> searchList = createSearchRequests(locationId);
        searchContract.createSearchRecords(searchList);
  }

    @Async("threadPoolSearchTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProducer(ProducerResponse producerResponse) {
      log.info("Updating producer search master record for  id: {}", producerResponse);
//      Producer producer = producerRepository.findById(producerResponse.producerId()).orElseThrow(() -> {
//          return new NotFoundException("producer not found");
//      });
//      producer.getLinesOfBusiness().forEach(line -> {
//          var lob = lobContract.findLineOfBusiness(line.getLineOfBusinessId());
//          searchContract.updateProducer(producer, lob, createProfileKeywords(producer.getKeywords(), lob));
//      });
    }

    @NotNull
    private List<SearchMasterRequest> createSearchRequests(@NotNull UUID locationId) {
        var profile = searchRepository.findProducerProfile(locationId)
                .orElseThrow(()-> {
                    log.error("No producer profile found for location {}", locationId);
                    return new IllegalStateException("No producer profile found for location " + locationId);
                });

        if ( profile.producer().getCancelDate().isBefore(OffsetDateTime.now())){
            log.error("Error creating search master record for  location {} - cancelDate {} is in the past",
                    locationId, profile.producer().getCancelDate());
            throw new PreconditionFailedException("Error creating search master record for location, producer cancelled " + locationId);
        }

    return profile.producer().getLinesOfBusiness().parallelStream()
        .map(
            pline -> {
              var lob = lobContract.findLineOfBusiness(pline.getLineOfBusinessId());
              return producerSearchMapper.toSearchMaster(
                  profile.producer(),
                  profile.location(),
                  profile.contact(),
                  lob,
                  createProfileKeywords(profile.producer().getKeywords(), lob));
            })
        .toList();
    }

    private String createProfileKeywords(String keywords, LineOfBusinessDto lob) {
        var services = lobContract.getServices(lob.lineOfBusinessId());
        final StringBuilder keywordsBuilder = new StringBuilder(keywords).append(" ").append(lob.lineOfBusinessName()).append(" ");
        services.forEach(service -> keywordsBuilder.append(service.getServiceName()).append(" "));
        return keywordsBuilder.toString();
    }

}
