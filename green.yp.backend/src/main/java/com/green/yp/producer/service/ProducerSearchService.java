package com.green.yp.producer.service;

import static java.lang.Boolean.TRUE;

import com.green.yp.api.apitype.PageableResponse;
import com.green.yp.api.apitype.ProducerServiceResponse;
import com.green.yp.api.apitype.enumeration.SearchRecordType;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.api.contract.LineOfBusinessContract;
import com.green.yp.api.contract.SearchContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.record.ProducerLocationDistanceProjection;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;
import com.green.yp.reference.dto.LineOfBusinessDto;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
                .ifPresentOrElse( loc -> createLocationSearchRecords(loc.getId()),
                        () -> {
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

      List<ProducerLocation> locations = locationRepository.findActiveLocations(producerResponse.producerId(), true);

      List<SearchMasterRequest> searchRequests = locations.parallelStream()
              .map(loc -> createSearchRequests(loc.getId()))
              .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

      searchContract.upsertSearchMaster(searchRequests, producerResponse.producerId());
    }

    public void deleteSearch(@NotNull @NonNull UUID serviceId,
                             @NotNull @NonNull SearchRecordType recordType) {
        log.info("Deleting product / service {} type {}", serviceId, recordType);
        searchContract.deleteSearchMaster(serviceId, recordType);
    }

    public void upsertProducerService(ProducerServiceResponse response) {
        log.info("Upserting producer service: {}", response);

        var profile = getProducerProfile(response.producerLocationId());
        var lob = lobContract.findLineOfBusiness(profile.producer().getPrimaryLineOfBusiness().getLineOfBusinessId());
        String keywordsBuilder = lob.lineOfBusinessName() + " " +
                                 response.shortDescription();
        searchContract.upsertSearchMaster(List.of(producerSearchMapper
                .toSearchMaster(response, profile.producer(), profile.location(), profile.contact(), lob, keywordsBuilder)),response.producerId());
    }

    public void upsertProduct(ProducerProductResponse response) {
        log.info("Upserting producer product: {}", response);

        var profile = getProducerProfile(response.producerLocationId());
        var lob = lobContract.findLineOfBusiness(profile.producer().getPrimaryLineOfBusiness().getLineOfBusinessId());
        String keywordsBuilder = lob.lineOfBusinessName() + " " +
                                 response.name();
        var productList = List.of(producerSearchMapper
                .toSearchMaster(response, profile.producer(), profile.location(), profile.contact(), lob, keywordsBuilder));

        searchContract.upsertSearchMaster(productList, response.producerId());
    }

    @NotNull
    private List<SearchMasterRequest> createSearchRequests(@NotNull UUID locationId) {
        var profile = getProducerProfile(locationId);

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

    private ProducerSearchRecord getProducerProfile(UUID locationId) {
        var profile = searchRepository.findProducerProfile(locationId)
                .orElseThrow(()-> {
                    log.error("No producer profile found for location {}", locationId);
                    return new IllegalStateException("No producer profile found for location " + locationId);
                });
        validateNotCancelled(locationId, profile);
        return profile;
    }

    private static void validateNotCancelled(UUID locationId, ProducerSearchRecord profile) {
        if (profile.producer().getCancelDate() != null
            && profile.producer().getCancelDate().isBefore(OffsetDateTime.now())){
            log.error("Error creating search master record for  location {} - cancelDate {} is in the past",
                    locationId, profile.producer().getCancelDate());
            throw new PreconditionFailedException("Error creating search master record for location, producer cancelled " + locationId);
        }
    }

    private String createProfileKeywords(String keywords, LineOfBusinessDto lob) {
        var services = lobContract.getServices(lob.lineOfBusinessId());
        final StringBuilder keywordsBuilder = new StringBuilder(keywords).append(" ").append(lob.lineOfBusinessName()).append(" ");
        services.forEach(service -> keywordsBuilder.append(service.getServiceName()).append(" "));
        return keywordsBuilder.toString();
    }
}
