package com.green.yp.producer.service;

import com.green.yp.api.apitype.search.SearchResponse;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.producer.data.record.ProducerLocationDistanceProjection;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class ProducerSearchService {

  private static final double MILES_IN_METERS = 1609.34d;
  private final ProducerSearchRepository searchRepository;
  private final GeocodingService geocodingService;
  private final ProducerSearchMapper producerSearchMapper;

  public ProducerSearchService(
      ProducerSearchRepository searchRepository,
      @Qualifier("defaultGeocodeServiceImpl") GeocodingService geocodingService,
      ProducerSearchMapper producerSearchMapper) {
    this.searchRepository = searchRepository;
    this.geocodingService = geocodingService;
    this.producerSearchMapper = producerSearchMapper;
  }

  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  public SearchResponse search(
      String zipCode, Integer distance, Pageable pageable, UUID categoryId, UUID serviceId) {
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

    return new SearchResponse(
        producerSearchMapper.toResponse(searchResults),
        (int) searchLocations.getTotalElements(),
        searchLocations.getNumber(),
        searchLocations.getTotalPages());
  }
}
