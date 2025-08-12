package com.green.yp.search.mapper;

import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.api.apitype.search.SearchResponse;
import com.green.yp.search.data.entity.SearchMaster;
import com.green.yp.search.data.entity.SearchRecord;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SearchMapper {

  List<SearchResponse> toResponse(List<SearchRecord> searchResults);

  @Mapping(target = "distance", source = "distanceMiles")
  @Mapping(target = "externId", source = "searchMaster.externId")
  @Mapping(target = "producerId", source = "searchMaster.producerId")
  @Mapping(target = "locationId", source = "searchMaster.locationId")
  @Mapping(target = "categoryRef", source = "searchMaster.categoryRef")
  @Mapping(target = "categoryName", source = "searchMaster.categoryName")
  @Mapping(target = "recordType", source = "searchMaster.recordType")
  @Mapping(target = "active", source = "searchMaster.active")
  @Mapping(target = "lastActiveDate", source = "searchMaster.lastActiveDate")
  @Mapping(target = "keywords", source = "searchMaster.keywords")
  @Mapping(target = "title", source = "searchMaster.title")
  @Mapping(target = "businessName", source = "searchMaster.businessName")
  @Mapping(target = "businessUrl", source = "searchMaster.businessUrl")
  @Mapping(target = "businessIconUrl", source = "searchMaster.businessIconUrl")
  @Mapping(target = "imageUrl", source = "searchMaster.imageUrl")
  @Mapping(target = "addressLine1", source = "searchMaster.addressLine1")
  @Mapping(target = "addressLine2", source = "searchMaster.addressLine2")
  @Mapping(target = "city", source = "searchMaster.city")
  @Mapping(target = "state", source = "searchMaster.state")
  @Mapping(target = "postalCode", source = "searchMaster.postalCode")
  @Mapping(target = "emailAddress", source = "searchMaster.emailAddress")
  @Mapping(target = "phoneNumber", source = "searchMaster.phoneNumber")
  @Mapping(target = "minPrice", source = "searchMaster.minPrice")
  @Mapping(target = "maxPrice", source = "searchMaster.maxPrice")
  @Mapping(target = "priceUnitsType", source = "searchMaster.priceUnitsType")
  @Mapping(target = "longitude", source = "searchMaster.longitude")
  @Mapping(target = "latitude", source = "searchMaster.latitude")
  @Mapping(target = "description", source = "searchMaster.description")
  SearchResponse toResponse(SearchRecord searchResult);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "lastUpdateDate", ignore = true)
  @Mapping(target = "active", ignore = true)
    SearchMaster toEntity(@NotNull SearchMasterRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "externId",  ignore = true)
    @Mapping(target = "customerRef", ignore = true)
    void upsertClassified(SearchMasterRequest request, @MappingTarget SearchMaster sm);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "externId",  ignore = true)
    @Mapping(target = "producerId", ignore = true)
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "categoryRef", ignore = true)
    @Mapping(target = "customerRef", ignore = true)
    void upsertProducer(SearchMasterRequest request, @MappingTarget SearchMaster sm);
}
