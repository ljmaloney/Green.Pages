package com.green.yp.email.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.email.data.repository.ContactMessageRepository;
import com.green.yp.email.mapper.ContactMapper;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageDataService {
    private final ContactMessageRepository repository;
    private final ContactMapper mapper;

    public MessageDataService(ContactMessageRepository repository, ContactMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ContactMessageResponse> findBetween(@NotNull OffsetDateTime startDate, @NotNull OffsetDateTime endDate){
        log.debug("Finding contact messages between {} and {}", startDate, endDate);
        return repository.findMessages(startDate, endDate);
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest request,
                                                       ProducerResponse producer,
                                                       ProducerLocationResponse location,
                                                       ProducerContactResponse contact,
                                                       String requestIP) {
        log.info("Creating new subscriber -> customer contact message - {} for {} at {} - IP {}",
                request.subject(), producer.producerId(), location.locationId(), requestIP);
        var contactMessage = mapper.toEntity(request, producer, location, contact, requestIP);
        contactMessage.setSourceIpAddress(requestIP);
        contactMessage.setSmsEmailType("email");
        contactMessage.setAddresseeName(getContactName(contact));
        contactMessage.setDestination(contact.emailAddress());
        if ( request.leadContactRequest().productServiceRef() != null ) {
            contactMessage.setProductServiceRef(request.leadContactRequest().productServiceRef());
        }
        return mapper.toDto(repository.saveAndFlush(contactMessage));
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest request,
                                                       ClassifiedAdCustomerResponse classified,
                                                       String requestIP) {
        log.info("Creating new classified ad -> customer contact message for classified id {}",
                request.classifiedRequest().classifiedId());

        var contactMessage = mapper.toEntity(request, classified, requestIP);
        contactMessage.setAddresseeName(String.join(" ", classified.customer().firstName(),classified.customer().lastName()));
        contactMessage.setSourceIpAddress(requestIP);
        contactMessage.setSmsEmailType("email");
        contactMessage.setDestination(classified.customer().emailAddress());
        return mapper.toDto(repository.saveAndFlush(contactMessage));
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest request,
                                                       String emailAddress,
                                                       String requestIP) {
        log.info("Creating new generic contact message for {}", emailAddress);
        var contactMessage = mapper.toEntity(request, requestIP);
        contactMessage.setAddresseeName(emailAddress);
        contactMessage.setSourceIpAddress(requestIP);
        contactMessage.setSmsEmailType("email");
        contactMessage.setDestination(emailAddress);

        return mapper.toDto(repository.saveAndFlush(contactMessage));
    }
    private String getContactName(ProducerContactResponse contact) {
        return StringUtils.isNotBlank(contact.genericContactName())
                ? contact.genericContactName()
                : String.join(" ", contact.firstName(),contact.lastName());
    }

}
