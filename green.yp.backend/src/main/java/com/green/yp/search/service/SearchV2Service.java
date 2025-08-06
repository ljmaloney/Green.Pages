package com.green.yp.search.service;

import com.green.yp.api.apitype.PageableResponse;
import com.green.yp.api.apitype.search.SearchResponse;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.search.data.entity.SearchDistanceProjection;
import com.green.yp.search.data.repository.SearchRepository;
import com.green.yp.search.mapper.SearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SearchV2Service {

    private static final double MILES_IN_METERS = 1609.34d;
    private final SearchRepository searchRepository;
    private final GeocodingService geocodingService;
    private final SearchMapper searchMapper;

    public SearchV2Service(SearchRepository searchRepository,
                           @Qualifier("defaultGeocodeServiceImpl") GeocodingService geocodingService,
                           SearchMapper searchMapper) {
        this.searchRepository = searchRepository;
        this.geocodingService = geocodingService;
        this.searchMapper = searchMapper;
    }

    public PageableResponse<SearchResponse> search(String zipCode, Integer distance, UUID categoryRefId, String keywords, Pageable pageable) {
        log.info("Searching for producers near zipCode: {}, within {} miles", zipCode, distance);

        var coordinates = geocodingService.getCoordinates(zipCode);
        var wktPoint = String.format("POINT(%f %f)", coordinates.latitude(), coordinates.longitude());

        BigDecimal distanceMeters = BigDecimal.valueOf(distance).multiply(BigDecimal.valueOf(MILES_IN_METERS));

    var searchLocations =
        searchRepository.executeSearch(
            wktPoint, distanceMeters, categoryRefId, StringUtils.isBlank(keywords) ? null : keywords, pageable);

        List<UUID> searchIds =
                searchLocations.get().map(SearchDistanceProjection::getId).toList();

        var searchResults =
                searchRepository.loadSearchResults(
                        searchIds,
                        coordinates.latitude().doubleValue(),
                        coordinates.longitude().doubleValue());

        log.info(
                "Found {} producers near zipCode: {}, within {} miles",
                searchResults.size(),
                zipCode,
                distance);

        return new PageableResponse<>(
                searchMapper.toResponse(searchResults),
                (int) searchLocations.getTotalElements(),
                searchLocations.getNumber(),
                searchLocations.getTotalPages());
    }
}
