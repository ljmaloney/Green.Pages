package com.green.yp.email.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MessageSendService {
  ContactMessageResponse createContactMessage(
      ContactMessageRequest contactMessageRequest, String requestIP);

  void sendMessage(UUID contactMessageId);
}
