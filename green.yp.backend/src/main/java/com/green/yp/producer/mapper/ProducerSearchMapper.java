package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.producer.data.model.ProducerLocationHours;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import java.util.List;
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

  List<TruncatedProducerResponse> limitedOutputResponse(List<ProducerSearchRecord> producersByLineOfBusiness);
  @Mapping(target = "producerId", source = "producer.id")
  @Mapping(target = "producerLocationId", source = "location.id")
  @Mapping(target = "businessName", source = "producer.name")
  @Mapping(target = "websiteUrl", source = "location.websiteUrl")
  @Mapping(target = "phone", source = "contact.phoneNumber")
  @Mapping(target = "city", source = "location.city")
  @Mapping(target = "state", source = "location.state")
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
  ProducerProfileResponse toProfileResponse(ProducerSearchRecord searchRecord);
  List<LocationHoursResponse> toHoursResponse(List<ProducerLocationHours> locationHours);
  LocationHoursResponse toResponse(ProducerLocationHours locationHours);
}
