package com.green.yp.contact.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.contact.data.repository.ContactMessageRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class ContactMessageDataService {
    private final ContactMessageRepository repository;

    public ContactMessageDataService(ContactMessageRepository repository){
        this.repository = repository;
    }

    public List<ContactMessageResponse> findBetween(@NotNull OffsetDateTime startDate, @NotNull OffsetDateTime endDate){
        return repository.findByCreateDateBetweenSortedByCreatedDateDesc(startDate, endDate);
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest,
                                                       ProducerResponse producer,
                                                       ProducerLocationResponse location,
                                                       ProducerContactResponse contact,
                                                       String requestIP) {
        return null;
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest,
                                                       ClassifiedAdCustomerResponse classified,
                                                       String requestIP) {
        return null;
    }

    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest,
                                                       String emailAddress,
                                                       String requestIP) {
        return null;
    }
}
