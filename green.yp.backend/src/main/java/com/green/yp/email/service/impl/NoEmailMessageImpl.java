package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.service.MessageDataService;
import com.green.yp.email.service.MessageSendService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("noEmail")
public class NoEmailMessageImpl implements MessageSendService {

  @Value("${greenyp.support.email:noemail}")
  private String supportEmail;

  private final MessageDataService messageDataService;

  public NoEmailMessageImpl(MessageDataService messageDataService) {
    this.messageDataService = messageDataService;
  }

  @Override
  public ContactMessageResponse createContactMessage(
      ContactMessageRequest contactMessageRequest, String requestIP) {
    log.info("Create support message request: {}", contactMessageRequest);
    return messageDataService.createContactMessage(contactMessageRequest, supportEmail, requestIP);
  }

  @Override
  public void sendMessage(UUID contactMessageId) {}
}
