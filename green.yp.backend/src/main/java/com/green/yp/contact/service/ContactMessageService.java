package com.green.yp.contact.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.*;
import com.green.yp.exception.PreconditionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ContactMessageService {
    private final EmailContract emailContract;
    private final ProducerContactContract contactContract;
    private final ContactMessageDataService messageDataService;
    private final ProducerContract producerContract;
    private final ProducerLocationContract locationContract;
    private final ClassifiedContract classifiedContract;

    public ContactMessageService(EmailContract contract,
                                 ProducerContract producerContract,
                                 ProducerContactContract producerContactContract,
                                 ProducerLocationContract locationContract,
                                 ClassifiedContract classifiedContract,
                                 ContactMessageDataService dataService){
        this.emailContract = contract;
        this.messageDataService = dataService;
        this.contactContract = producerContactContract;
        this.producerContract = producerContract;
        this.locationContract = locationContract;
        this.classifiedContract = classifiedContract;
    }
    public void sendMessage(ContactMessageRequest contactMessageRequest, String requestIP) {
        ContactMessageResponse messageResponse;
        if ( contactMessageRequest.leadContactRequest() != null){
            var producer = producerContract.findProducer(contactMessageRequest.leadContactRequest().producerId());

          var location = locationContract.findLocation(contactMessageRequest.leadContactRequest().locationId());

          var contact = contactContract.findContacts(producer.producerId(), location.locationId())
                  .stream()
                  .filter( c -> c.producerContactType() == ProducerContactType.SALES
                                || c.producerContactType() == ProducerContactType.PRIMARY)
                  .findAny()
                  .orElseThrow( () -> new PreconditionFailedException("Missing primary or sales contact for message"));

            messageResponse = messageDataService.createContactMessage(contactMessageRequest, producer, location, contact, requestIP);
        } else if ( contactMessageRequest.classifiedRequest() != null ) {
            var classified = classifiedContract.findClassifiedAd(contactMessageRequest.classifiedRequest().classifiedId());
            messageResponse = messageDataService.createContactMessage(contactMessageRequest, classified, requestIP);
        } else {
            var emailAddress = "";
            messageResponse = messageDataService.createContactMessage(contactMessageRequest, emailAddress, requestIP);
        }
    }
}
