package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.ClassifiedAdTypeResponse;
import com.green.yp.classifieds.data.model.ClassifiedAdType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedAdTypeMapper {
  @Mapping(source = "id", target = "adTypeId")
  ClassifiedAdTypeResponse fromEntity(ClassifiedAdType entity);
}
