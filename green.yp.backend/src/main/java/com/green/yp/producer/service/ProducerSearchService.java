package com.green.yp.producer.service;

import com.green.yp.api.apitype.search.SearchResponse;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.producer.data.record.ProducerLocationDistanceProjection;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class ProducerSearchService {

  private final ProducerSearchRepository searchRepository;
  private final GeocodingService geocodingService;
  private final ProducerSearchMapper producerSearchMapper;

  public ProducerSearchService(
      ProducerSearchRepository searchRepository,
      GeocodingService geocodingService,
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
    var wktPoint = String.format("POINT(%f %f)", coordinates.longitude(), coordinates.latitude());
    var searchLocations = searchRepository.findProducersWithinDistance(wktPoint, distance, categoryId, pageable);

    List<UUID> locationIds = searchLocations.get()
            .map(foundLocation -> foundLocation.getLocationId())
            .toList();

    var searchResults = searchRepository.findProducers(locationIds, distance,
            coordinates.latitude().doubleValue(), coordinates.longitude().doubleValue());

    log.info("Found {} producers near zipCode: {}, within {} miles", searchResults.size(), zipCode, distance);

    return new SearchResponse(
        producerSearchMapper.toResponse(searchResults),
        (int) searchLocations.getTotalElements(),
            searchLocations.getNumber(),
            searchLocations.getTotalPages());
  }
}
