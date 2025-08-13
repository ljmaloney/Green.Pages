package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.service.MessageSendService;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

@Qualifier("classifiedMessage")
public class ClassifiedMessageImpl implements MessageSendService {
    @Override
    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest, String requestIP) {
        return null;
    }

    @Override
    public void sendMessage(UUID contactMessageId) {

    }
}
