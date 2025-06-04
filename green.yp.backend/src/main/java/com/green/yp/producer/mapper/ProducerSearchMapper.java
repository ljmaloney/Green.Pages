package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.search.ProducerSearchResponse;
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
}
