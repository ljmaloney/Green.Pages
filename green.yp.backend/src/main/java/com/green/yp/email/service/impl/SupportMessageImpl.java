package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.data.model.ContactMessage;
import com.green.yp.email.mapper.ContactMapper;
import com.green.yp.email.service.MessageDataService;
import com.green.yp.email.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Slf4j
@Qualifier("supportMessage")
public class SupportMessageImpl implements MessageSendService {

  @Value("${greenyp.support.email:noemail}")
  private String supportEmail;

    private final MessageDataService messageDataService;
    private final ContactMapper contactMapper;

    public SupportMessageImpl(MessageDataService messageDataService, ContactMapper contactMapper) {
        this.messageDataService = messageDataService;
        this.contactMapper = contactMapper;
    }

    @Override
    public ContactMessageResponse createContactMessage(ContactMessageRequest contactMessageRequest, String requestIP) {
        log.info("Create support message request: {}", contactMessageRequest);
        return messageDataService.createContactMessage(contactMessageRequest, supportEmail, requestIP);
    }

    @Override
    public void sendMessage(UUID contactMessageId) {

    }
}
