package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.*;
import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import com.green.yp.classifieds.data.model.ClassifiedCustomerProjection;
import jakarta.validation.Valid;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedMapper {

  @Mapping(source = "id", target = "customerId")
  ClassifiedCustomerResponse fromEntity(ClassifiedCustomer classifiedCustomer);

  @Mapping(source = "id", target = "classifiedId")
  ClassifiedResponse fromEntity(Classified classified);

  ClassifiedAdCustomerResponse fromProjection(
      ClassifiedCustomerProjection classifiedCustomerProjection);

  ClassifiedCustomer customterFromClassified(ClassifiedRequest request);

  @Mapping(source = "pricePerUnitType", target = "perUnitType")
  @Mapping(source = "adType", target = "adTypeId")
  @Mapping(source = "categoryId", target = "categoryId")
  Classified toEntity(@Valid ClassifiedRequest request);

    @Mapping(target = "externId", source = "classified.id")
    @Mapping(target = "producerId", ignore = true)
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "categoryRef", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "recordType", constant = "CLASSIFIED")
    @Mapping(target = "lastActiveDate", source = "classified.lastActiveDate")
    @Mapping(target = "keywords", source = "keywords")
    @Mapping(target = "title", source = "classified.title")
    @Mapping(target = "businessName", ignore = true)
    @Mapping(target = "businessUrl", ignore = true)
    @Mapping(target = "businessIconUrl", ignore = true)
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "addressLine1", ignore = true)
    @Mapping(target = "addressLine2", ignore = true)
    @Mapping(target = "city", source = "classified.city")
    @Mapping(target = "state", source = "classified.state")
    @Mapping(target = "postalCode", source = "classified.postalCode")
    @Mapping(target = "emailAddress", source = "classified.emailAddress")
    @Mapping(target = "phoneNumber", source = "classified.phoneNumber")
    @Mapping(target = "minPrice", source = "classified.price")
    @Mapping(target = "maxPrice", ignore = true)
    @Mapping(target = "priceUnitsType", source = "classified.perUnitType")
    @Mapping(target = "longitude", source = "classified.longitude")
    @Mapping(target = "latitude", source = "classified.latitude")
    @Mapping(target = "description", source = "classified.description")
    @Mapping(target = "customerRef", ignore = true)
    SearchMasterRequest toSearchRequest(Classified classified,
                                        ClassifiedCustomer customer,
                                        ClassifiedCategoryResponse category,
                                        String imageUrl,
                                        String keywords);
}
