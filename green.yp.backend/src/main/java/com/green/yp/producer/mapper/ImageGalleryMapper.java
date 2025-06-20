package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.producer.data.model.ImageGallery;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ImageGalleryMapper {
    List<ProducerImageResponse> mapToResponse(List<ImageGallery> images);
    ProducerImageResponse mapToResponse(ImageGallery image);
}
