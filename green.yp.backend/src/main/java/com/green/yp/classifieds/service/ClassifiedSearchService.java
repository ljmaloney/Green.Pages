package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedSearchResponse;
import com.green.yp.classifieds.data.model.ClassifiedAdType;
import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import com.green.yp.classifieds.data.model.ClassifiedSearchProjection;
import com.green.yp.classifieds.data.repository.ClassifiedImageGalleryRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ClassifiedSearchService {

    private final ClassifiedRepository repository;
    private final ClassifiedSearchMapper mapper;
    private final ClassifiedImageGalleryRepository imageGalleryRepository;

    public ClassifiedSearchService(ClassifiedRepository repository,
                                   ClassifiedSearchMapper mapper,
                                   ClassifiedImageGalleryRepository imageGalleryRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.imageGalleryRepository = imageGalleryRepository;
    }

    public List<ClassifiedSearchResponse> mostRecent(Integer maxCount, UUID categoryId) {
        log.debug("Loading most {} recent classifieds for categoryId: {}", maxCount, categoryId );

        List<ClassifiedSearchProjection> classifieds = repository.getMostRecent(categoryId, Limit.of(maxCount));

        return classifieds.stream()
                .map(ad -> mapper.fromProjection(ad.classified(),
                        ad.adType(), ad.category(), findImage(ad)))
                .toList();
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
