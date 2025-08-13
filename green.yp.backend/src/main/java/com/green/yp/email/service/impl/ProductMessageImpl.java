package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.service.MessageSendService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("productMessage")
public class ProductMessageImpl implements MessageSendService {
    @Override
    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest, String requestIP) {
        return null;
    }

    @Override
    public void sendMessage(UUID contactMessageId) {

    }
}
