package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.ProducerServiceResponse;
import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.api.apitype.search.SearchMasterRequest;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.producer.data.model.*;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import java.util.List;
import java.util.UUID;

import com.green.yp.reference.dto.LineOfBusinessDto;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProducerSearchMapper {
  List<ProducerSearchResponse> toResponse(List<ProducerSearchRecord> records);

  @Mapping(target = "producerId", source = "producer.id")
  @Mapping(target = "producerLocationId", source = "location.id")
  @Mapping(target = "businessName", source = "producer.name")
  @Mapping(target = "businessNarrative", source = "producer.narrative")
  @Mapping(target = "latitude", source = "location.latitude")
  @Mapping(target = "longitude", source = "location.longitude")
  @Mapping(target = "websiteUrl", source = "location.websiteUrl")
  @Mapping(target = "phone", source = "contact.phoneNumber")
  @Mapping(target = "cellPhone", source = "contact.cellPhoneNumber")
  @Mapping(target = "distance", source = "distance")
  @Mapping(target = "addressLine1", source = "location.addressLine1")
  @Mapping(target = "addressLine2", source = "location.addressLine2")
  @Mapping(target = "addressLine3", source = "location.addressLine3")
  @Mapping(target = "city", source = "location.city")
  @Mapping(target = "state", source = "location.state")
  @Mapping(target = "postalCode", source = "location.postalCode")
  @Mapping(target = "iconLink", source = "producer.iconLink")
  ProducerSearchResponse toResponse(ProducerSearchRecord searchRecord);

  List<TruncatedProducerResponse> limitedOutputResponse(
      List<ProducerSearchRecord> producersByLineOfBusiness);

  @Mapping(target = "producerId", source = "producer.id")
  @Mapping(target = "producerLocationId", source = "location.id")
  @Mapping(target = "businessName", source = "producer.name")
  @Mapping(target = "websiteUrl", source = "location.websiteUrl")
  @Mapping(target = "phone", source = "contact.phoneNumber")
  @Mapping(target = "city", source = "location.city")
  @Mapping(target = "state", source = "location.state")
  @Mapping(target = "postalCode", source="location.postalCode")
  @Mapping(target = "iconLink", source = "producer.iconLink")
  TruncatedProducerResponse limitedOutputResponse(ProducerSearchRecord searchRecord);

  @Mapping(target = "producerId", source = "producer.id")
  @Mapping(target = "locationId", source = "location.id")
  @Mapping(target = "businessName", source = "producer.name")
  @Mapping(target = "createDate", source = "producer.createDate")
  @Mapping(target = "lastUpdateDate", source = "producer.lastUpdateDate")
  @Mapping(target = "locationName", source = "location.locationName")
  @Mapping(target = "locationType", source = "location.locationType")
  @Mapping(target = "locationDisplayType", source = "location.locationDisplayType")
  @Mapping(target = "hasImagesUploaded", source = "producer.hasImagesUploaded")
  @Mapping(target = "locationHours", source = "location.locationHours")
  @Mapping(target = "businessNarrative", source = "producer.narrative")
  @Mapping(target = "latitude", source = "location.latitude")
  @Mapping(target = "longitude", source = "location.longitude")
  @Mapping(target = "websiteUrl", source = "location.websiteUrl")
  @Mapping(target = "phone", source = "contact.phoneNumber")
  @Mapping(target = "cellPhone", source = "contact.cellPhoneNumber")
  @Mapping(target = "addressLine1", source = "location.addressLine1")
  @Mapping(target = "addressLine2", source = "location.addressLine2")
  @Mapping(target = "addressLine3", source = "location.addressLine3")
  @Mapping(target = "city", source = "location.city")
  @Mapping(target = "emailAddress", source = "contact.emailAddress")
  @Mapping(target = "state", source = "location.state")
  @Mapping(target = "postalCode", source = "location.postalCode")
  @Mapping(target = "iconLink", source = "producer.iconLink")
  @Mapping(target = "subscriptionIds", source ="producer.subscriptionList")
  @Mapping(target = "keywords", source = "producer.keywords")
  ProducerProfileResponse toProfileResponse(ProducerSearchRecord searchRecord);

  default List<UUID> toSubscriptionIds(List<ProducerSubscription> producerSubscriptions){
    if ( CollectionUtils.isEmpty(producerSubscriptions)) {
      return List.of();
    }
    return producerSubscriptions.stream().map(ProducerSubscription::getSubscriptionId).toList();
  }

  List<LocationHoursResponse> toHoursResponse(List<ProducerLocationHours> locationHours);

  LocationHoursResponse toResponse(ProducerLocationHours locationHours);

  @Mapping(target = "externId", source = "location.id")
  @Mapping(target = "producerId", source = "producer.id")
  @Mapping(target = "locationId", source = "location.id")
  @Mapping(target = "categoryRef", source = "lob.lineOfBusinessId")
  @Mapping(target = "categoryName", source = "lob.lineOfBusinessName")
  @Mapping(target = "recordType", constant = "GREEN_PRO")
  @Mapping(target = "lastActiveDate", ignore = true)
  @Mapping(target = "keywords", source = "profileKeywords")
  @Mapping(target = "title", source = "location.locationName")
  @Mapping(target = "businessName", source = "producer.name")
  @Mapping(target = "businessUrl", source = "location.websiteUrl")
  @Mapping(target = "businessIconUrl", source = "producer.iconLink")
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "addressLine1", source = "location.addressLine1")
  @Mapping(target = "addressLine2", source = "location.addressLine2")
  @Mapping(target = "city", source = "location.city")
  @Mapping(target = "state", source = "location.state")
  @Mapping(target = "postalCode", source = "location.postalCode")
  @Mapping(target = "emailAddress", source = "contact.emailAddress")
  @Mapping(target = "phoneNumber", source = "contact.phoneNumber")
  @Mapping(target = "minPrice", ignore = true)
  @Mapping(target = "maxPrice", ignore = true)
  @Mapping(target = "priceUnitsType", ignore = true)
  @Mapping(target = "longitude", source = "location.longitude")
  @Mapping(target = "latitude", source = "location.latitude")
  @Mapping(target = "description", source = "producer.narrative")
  @Mapping(target = "customerRef", ignore = true)
  SearchMasterRequest toSearchMaster(
      Producer producer,
      ProducerLocation location,
      ProducerContact contact,
      LineOfBusinessDto lob,
      String profileKeywords);

    @Mapping(target = "externId", source = "service.producerServiceId")
    @Mapping(target = "producerId", source = "service.producerId")
    @Mapping(target = "locationId", source = "service.producerLocationId")
    @Mapping(target = "categoryRef", source = "lob.lineOfBusinessId")
    @Mapping(target = "categoryName", source = "lob.lineOfBusinessName")
    @Mapping(target = "recordType", constant = "GREEN_PRO_SERVICE")
    @Mapping(target = "lastActiveDate", ignore = true)
    @Mapping(target = "keywords", source = "keywords")
    @Mapping(target = "title", source = "service.shortDescription")
    @Mapping(target = "businessName", source = "producer.name")
    @Mapping(target = "businessUrl", source = "location.websiteUrl")
    @Mapping(target = "businessIconUrl", source = "producer.iconLink")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "addressLine1", source = "location.addressLine1")
    @Mapping(target = "addressLine2", source = "location.addressLine2")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "state", source = "location.state")
    @Mapping(target = "postalCode", source = "location.postalCode")
    @Mapping(target = "emailAddress", source = "contact.emailAddress")
    @Mapping(target = "phoneNumber", source = "contact.phoneNumber")
    @Mapping(target = "minPrice", source = "service.minServicePrice")
    @Mapping(target = "maxPrice", source = "service.maxServicePrice")
    @Mapping(target = "priceUnitsType",source = "service.priceUnitsType")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "description", source = "service.description")
    @Mapping(target = "customerRef", ignore = true)
    SearchMasterRequest toSearchMaster(ProducerServiceResponse service,
                          Producer producer,
                          ProducerLocation location,
                          ProducerContact contact,
                          LineOfBusinessDto lob,
                          String keywords);

    @Mapping(target = "externId", source = "product.productId")
    @Mapping(target = "producerId", source = "product.producerId")
    @Mapping(target = "locationId", source = "product.producerLocationId")
    @Mapping(target = "categoryRef", source = "lob.lineOfBusinessId")
    @Mapping(target = "categoryName", source = "lob.lineOfBusinessName")
    @Mapping(target = "recordType", constant = "GREEN_PRO_PRODUCT")
    @Mapping(target = "lastActiveDate", source = "product.discontinueDate")
    @Mapping(target = "keywords", source = "keywords")
    @Mapping(target = "title", source = "product.name")
    @Mapping(target = "businessName", source = "producer.name")
    @Mapping(target = "businessUrl", source = "location.websiteUrl")
    @Mapping(target = "businessIconUrl", source = "producer.iconLink")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "addressLine1", source = "location.addressLine1")
    @Mapping(target = "addressLine2", source = "location.addressLine2")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "state", source = "location.state")
    @Mapping(target = "postalCode", source = "location.postalCode")
    @Mapping(target = "emailAddress", source = "contact.emailAddress")
    @Mapping(target = "phoneNumber", source = "contact.phoneNumber")
    @Mapping(target = "minPrice", source = "product.price")
    @Mapping(target = "maxPrice", ignore = true)
    @Mapping(target = "priceUnitsType", source = "product.pricePerType")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "customerRef", ignore = true)
    SearchMasterRequest toSearchMaster(ProducerProductResponse product,
                                       Producer producer,
                                       ProducerLocation location,
                                       ProducerContact contact,
                                       LineOfBusinessDto lob,
                                       String keywords);
}
