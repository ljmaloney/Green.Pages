package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.ClassifiedSearchResponse;
import com.green.yp.classifieds.data.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedSearchMapper {

    @Mapping(target="categoryName", source = "category.name")
    @Mapping(target="classifiedId", source="classified.id")
    @Mapping(target = "description", source = "classified.description")
    @Mapping(target = "obscureContactInfo", source = "adType.features.protectContact")
    @Mapping(target = "url", expression = "java(getImageUrl(image))")
    @Mapping(target = "imageName", expression = "java(getImageName(image))")
    ClassifiedSearchResponse fromProjection(Classified classified,
                                            ClassifiedAdType adType,
                                            ClassifiedCategory category,
                                            Optional<ClassifiedImageGallery> image);

    default String getImageUrl(Optional<ClassifiedImageGallery> image) {
        return image.map(ClassifiedImageGallery::getUrl).orElse(null);
    }
    default String getImageName(Optional<ClassifiedImageGallery> image) {
        return image.map(ClassifiedImageGallery::getImageFilename).orElse(null);
    }

    List<ClassifiedSearchResponse> fromProjection(List<ClassifiedSearchProjection> searchProjections);

    @Mapping(target="categoryName", source = "category.name")
    @Mapping(target="classifiedId", source="classified.id")
    @Mapping(target = "description", source = "classified.description")
    @Mapping(target = "obscureContactInfo", source = "adType.features.protectContact")
    @Mapping(target = "url", expression = "java(getImageUrl(image))")
    @Mapping(target = "imageName", expression = "java(getImageName(image))")
    ClassifiedSearchResponse fromProjection(Classified classified,
                                            ClassifiedAdType classifiedAdType,
                                            ClassifiedCategory category,
                                            Optional<ClassifiedImageGallery> image,
                                            BigDecimal distance);
}
