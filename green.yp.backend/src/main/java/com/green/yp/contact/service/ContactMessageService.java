package com.green.yp.contact.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.contract.EmailContract;
import com.green.yp.api.contract.ProducerContactContract;
import com.green.yp.api.contract.ProducerContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContactMessageService {
    private final EmailContract emailContract;
    private final ProducerContactContract contactContract;
    private final ContactMessageDataService emailDataService;
    private final ProducerContract producerContract;

    public ContactMessageService(EmailContract contract,
                                 ProducerContract producerContract,
                                 ProducerContactContract producerContactContract,
                                 ContactMessageDataService dataService){
        this.emailContract = contract;
        this.emailDataService = dataService;
        this.contactContract = producerContactContract;
        this.producerContract = producerContract;
    }
    public void sendMessage(ContactMessageRequest contactMessageRequest, String requestIP) {
    }
}
