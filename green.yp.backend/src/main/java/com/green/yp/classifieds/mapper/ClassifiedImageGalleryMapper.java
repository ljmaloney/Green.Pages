package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.ClassifiedImageResponse;
import com.green.yp.classifieds.data.model.ClassifiedImageGallery;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedImageGalleryMapper {
    List<ClassifiedImageResponse> mapToResponse(List<ClassifiedImageGallery> images);
    ClassifiedImageResponse mapToResponse(ClassifiedImageGallery image);
}
