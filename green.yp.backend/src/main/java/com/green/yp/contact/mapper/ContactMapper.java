package com.green.yp.contact.mapper;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.contact.data.model.ContactMessage;
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

  @Mapping(target="createDate", ignore=true)
  @Mapping(target = "lastUpdateDate", ignore = true)
  @Mapping(target = "title", source = "request.subject")
  @Mapping(target = "fromEmail", source = "request.emailAddress")
  @Mapping(target = "fromPhone", source = "request.phoneNumber")
  @Mapping(target = "producerId", source= "producer.producerId")
   ContactMessage requestToEntity(ContactMessageRequest request,
                                   ProducerResponse producer,
                                   ProducerLocationResponse location,
                                   ProducerContactResponse contact);

    @Mapping(target="createDate", ignore=true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "title", source = "request.subject")
    @Mapping(target = "fromEmail", source = "request.emailAddress")
    @Mapping(target = "fromPhone", source = "request.phoneNumber")
    ContactMessage requestToEntity(ContactMessageRequest request,
                                   ClassifiedAdCustomerResponse classified);

    @Mapping(target="createDate", ignore=true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "title", source = "subject")
    @Mapping(target = "fromEmail", source = "emailAddress")
    @Mapping(target = "fromPhone", source = "phoneNumber")
    ContactMessage requestToEntity(ContactMessageRequest request);
}
