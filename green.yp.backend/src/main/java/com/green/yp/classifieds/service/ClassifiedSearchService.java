package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedSearchResponse;
import com.green.yp.classifieds.data.ClassifiedSearchDistanceProjection;
import com.green.yp.classifieds.data.model.ClassifiedAdType;
import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import com.green.yp.classifieds.data.model.ClassifiedSearchProjection;
import com.green.yp.classifieds.data.repository.ClassifiedImageGalleryRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedSearchMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.green.yp.common.dto.GenericPageableResponse;
import com.green.yp.geolocation.service.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedSearchService {

    private final ClassifiedRepository repository;
    private final ClassifiedSearchMapper mapper;
    private final ClassifiedImageGalleryRepository imageGalleryRepository;
    private final GeocodingService geocodingService;

    public ClassifiedSearchService(ClassifiedRepository repository,
                                   ClassifiedSearchMapper mapper,
                                   ClassifiedImageGalleryRepository imageGalleryRepository,
                                   @Qualifier("defaultGeocodeServiceImpl") GeocodingService geocodingService) {
        this.repository = repository;
        this.mapper = mapper;
        this.imageGalleryRepository = imageGalleryRepository;
        this.geocodingService = geocodingService;
    }

    public List<ClassifiedSearchResponse> mostRecent(Integer maxCount, UUID categoryId, String requestIP) {
        log.debug("Loading most {} recent classifieds for categoryId: {} requested from {}", maxCount, categoryId, requestIP );

        List<ClassifiedSearchProjection> classifieds = repository.getMostRecent(LocalDate.now(), categoryId, Limit.of(maxCount));

        return classifieds.stream()
                .map(ad -> mapper.fromProjection(ad.classified(),
                        ad.adType(), ad.category(), findImage(ad)))
                .toList();
    }

    public GenericPageableResponse<ClassifiedSearchResponse> searchClassifieds(Pageable pageable,
                                                                               String postalCode,
                                                                               Integer distanceMiles,
                                                                               UUID classifiedCategoryId,
                                                                               String keywords,
                                                                               String requestIP) {
        log.info("Searching classifieds for using postalCode {}, maxDistance {}, classifiedCategoryId {}. keywords [{}] from IP {}",
                postalCode, distanceMiles, classifiedCategoryId, keywords, requestIP );

        var coordinates = geocodingService.getCoordinates(postalCode);
        var wktPoint = String.format("POINT(%f %f)", coordinates.latitude(), coordinates.longitude());

        BigDecimal distanceMeters = BigDecimal.valueOf(distanceMiles).multiply(BigDecimal.valueOf(1609.34d));

        keywords = StringUtils.isBlank(keywords) ? null : keywords;

        var searchPageResults = repository.searchClassifieds(wktPoint,
                distanceMeters, LocalDate.now(), classifiedCategoryId, keywords, pageable);

        List<UUID> classifiedIds = searchPageResults
                .stream()
                .map(ClassifiedSearchDistanceProjection::getClassifiedId)
                .toList();

        List<ClassifiedSearchProjection> searchProjections = repository.getSearchRsults(classifiedIds,
                coordinates.latitude().doubleValue(), coordinates.longitude().doubleValue());

        log.info("Found classifieds for using postalCode {}, maxDistance {}, classifiedCategoryId {}. keywords [{}] from IP {}",
                postalCode, distanceMiles, classifiedCategoryId, keywords, requestIP );

        var images = imageGalleryRepository.findImagesByClassifiedIds(classifiedIds);

        var responses = searchProjections
                .stream()
                .map( sp -> mapper.fromProjection(sp.classified(),
                        sp.adType(), sp.category(), findImage(images, sp.classified().getId()), sp.distance()))
                .toList();

        return new GenericPageableResponse<>(
                responses,
                (int) searchPageResults.getTotalElements(),
                searchPageResults.getNumber(),
                searchPageResults.getTotalPages());
    }

    private Optional<ClassifiedImageGallery> findImage(List<ClassifiedImageGallery> images, UUID classifiedId) {
        return images.stream()
                .filter( image -> image.getClassifiedId().equals(classifiedId))
                .findFirst();
    }

    private Optional<ClassifiedImageGallery> findImage(ClassifiedSearchProjection classified) {
        if ( hasImageFeature(classified.adType())) {
            return imageGalleryRepository.findFirstByClassifiedId(classified.classified().getId());
        }
        return Optional.empty();
    }

    private boolean hasImageFeature(ClassifiedAdType classifiedAdType) {
        return classifiedAdType.getFeatures().maxImages() > 0;
    }
}
