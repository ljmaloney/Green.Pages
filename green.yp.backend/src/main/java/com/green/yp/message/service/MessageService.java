package com.green.yp.message.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

  private final Map<String, MessageSendService> senders;

  public MessageService(Map<String, MessageSendService> senders) {
    this.senders = senders;
    log.info("Initialized MessageService with {} senders", senders.size());
  }

  public void sendMessage(ContactMessageRequest contactRequest, String requestIP) {
    log.info(
        "Sending {} message re {} from {}",
        contactRequest.requestType(),
        contactRequest.subject(),
        requestIP);

    MessageSendService sender = senders.get(contactRequest.requestType().getEmailSender());
    if (sender == null) {
      log.info("No sender registered for {}", contactRequest.requestType());
    }
    ContactMessageResponse contactMessageResponse =
        sender.createContactMessage(contactRequest, requestIP);

    sender.sendMessage(contactMessageResponse.emailMessageId());
  }
}
