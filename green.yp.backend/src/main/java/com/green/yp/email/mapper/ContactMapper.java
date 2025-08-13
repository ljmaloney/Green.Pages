package com.green.yp.email.mapper;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.email.data.model.ContactMessage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContactMapper {

  ContactMessageResponse toDto(ContactMessage contactMessage);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "lastUpdateDate", ignore = true)
  @Mapping(target = "title", source = "request.subject")
  @Mapping(target = "requestorName", source = "request.name")
  @Mapping(target = "fromEmail", source = "request.emailAddress")
  @Mapping(target = "fromPhone", source = "request.phoneNumber")
  @Mapping(target = "producerId", source = "producer.producerId")
  @Mapping(target = "sourceIpAddress", source = "ipAddress")
  @Mapping(target = "contactRequestType", source = "request.requestType")
  ContactMessage toEntity(
      ContactMessageRequest request,
      ProducerResponse producer,
      ProducerLocationResponse location,
      ProducerContactResponse contact,
      String ipAddress);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "lastUpdateDate", ignore = true)
  @Mapping(target = "title", source = "request.subject")
  @Mapping(target = "fromEmail", source = "request.emailAddress")
  @Mapping(target = "fromPhone", source = "request.phoneNumber")
  @Mapping(target = "sourceIpAddress", source = "ipAddress")
  @Mapping(target = "destination", source = "classified.classified.emailAddress")
  @Mapping(target = "contactRequestType", source = "request.requestType")
  ContactMessage toEntity(
      ContactMessageRequest request, ClassifiedAdCustomerResponse classified, String ipAddress);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "lastUpdateDate", ignore = true)
  @Mapping(target = "title", source = "request.subject")
  @Mapping(target = "fromEmail", source = "request.emailAddress")
  @Mapping(target = "fromPhone", source = "request.phoneNumber")
  @Mapping(target = "sourceIpAddress", source = "ipAddress")
  @Mapping(target = "contactRequestType", source = "request.requestType")
  ContactMessage toEntity(ContactMessageRequest request, String ipAddress);
}
