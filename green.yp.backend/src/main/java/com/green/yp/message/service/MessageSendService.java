package com.green.yp.message.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface MessageSendService {
  ContactMessageResponse createContactMessage(
      ContactMessageRequest contactMessageRequest, String requestIP);

  void sendMessage(UUID contactMessageId);
}
