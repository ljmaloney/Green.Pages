package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.producer.data.model.ProducerContact;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerContactMapper {

  ProducerContact toEntity(ProducerContactRequest createContactRequest);

  @Mapping(source = "contact.id", target = "contactId")
  ProducerContactResponse fromEntity(ProducerContact contact);

  List<ProducerContactResponse> fromEntity(List<ProducerContact> contacts);

  @Mapping(source = "request.producerContactType", target = "producerContactType")
  @Mapping(source = "request.displayContactType", target = "displayContactType")
  @Mapping(source = "request.genericContactName", target = "genericContactName")
  @Mapping(source = "request.firstName", target = "firstName")
  @Mapping(source = "request.lastName", target = "lastName")
  @Mapping(source = "request.phoneNumber", target = "phoneNumber")
  @Mapping(source = "request.cellPhoneNumber", target = "cellPhoneNumber")
  @Mapping(source = "request.emailAddress", target = "emailAddress")
  @Mapping(source = "request.producerLocationId", target = "producerLocationId")
  @Mapping(source = "contact.version", target = "version")
  ProducerContact copy(ProducerContact contact, ProducerContactRequest request);
}
