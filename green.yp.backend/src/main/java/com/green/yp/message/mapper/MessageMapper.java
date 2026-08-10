package com.green.yp.message.mapper;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.message.data.model.Message;
import com.green.yp.message.data.model.MessageMeta;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MessageMapper {

    @Mapping(target = "contactRequestType", source = "request.requestType")
    @Mapping(target = "subject", source = "request.subject")
    @Mapping(target = "sourceRef", source = "classifiedCustomer.classified.classifiedId")
    @Mapping(target = "parentSourceRef", source="classifiedCustomer.classified.classifiedId")
    @Mapping(target = "requestorName", source="request.name")
    @Mapping(target = "fromEmail", source="request.emailAddress")
    @Mapping(target = "fromPhone", source = "request.phoneNumber")
    @Mapping(target = "companyName", source="request.companyName")
    MessageMeta toEntity(ContactMessageRequest request,
                         ClassifiedAdCustomerResponse classifiedCustomer);

    @Mapping(target = "message", source = "request.message")
    @Mapping(target = "addresseeRef", source = "classifiedCustomer.classified.classifiedId")
    Message toMessageEntity(ContactMessageRequest request,
                            ClassifiedAdCustomerResponse classifiedCustomer);


    @Mapping(target = "emailMessageId", source = "message.id")
    @Mapping(target = "createDate", source = "savedMessage.createDate")
    @Mapping(target = "messageSendDate", source = "message.messageSentDate")
    @Mapping(target = "sourceIpAddress", source = "savedMessage.sourceIpAddress")
    @Mapping(target = "requestType", source = "savedMessage.contactRequestType")
    @Mapping(target = "fromEmail", source = "message.fromEmail")
    @Mapping(target = "fromPhone", source = "message.fromPhone")
    @Mapping(target = "addresseeName", source = "message.addresseeName")
    @Mapping(target = "title", source = "savedMessage.subject")
    @Mapping(target = "message", source = "message.message")
    ContactMessageResponse toResponse(MessageMeta savedMessage,
                                      Message message);
}
