package com.green.yp.classifieds.mapper;

import com.green.yp.classifieds.apitype.ClassifiedAdTypeResponse;
import com.green.yp.classifieds.data.model.ClassifiedAdType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedAdTypeMapper {
  ClassifiedAdTypeResponse fromEntity(ClassifiedAdType entity);
}
