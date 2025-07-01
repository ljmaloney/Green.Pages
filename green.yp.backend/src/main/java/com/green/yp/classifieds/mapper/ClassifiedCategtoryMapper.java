package com.green.yp.classifieds.mapper;

import com.green.yp.classifieds.apitype.ClassifiedCategoryResponse;
import com.green.yp.classifieds.data.model.ClassifiedCategory;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedCategtoryMapper {
    @Mapping(source="id", target="categoryId")
    ClassifiedCategoryResponse fromEntity(ClassifiedCategory classifiedCategory);
}
