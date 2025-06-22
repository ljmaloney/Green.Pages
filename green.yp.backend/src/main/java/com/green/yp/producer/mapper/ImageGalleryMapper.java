package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.producer.data.model.ImageGallery;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ImageGalleryMapper {
    List<ProducerImageResponse> mapToResponse(List<ImageGallery> images);
    ProducerImageResponse mapToResponse(ImageGallery image);
}
