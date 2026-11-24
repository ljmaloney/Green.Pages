package com.green.yp.message.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface MessageSendService {
   Optional<ContactMessageResponse> createContactMessage(
      ContactMessageRequest contactMessageRequest, String requestIP);

  void sendMessage(UUID contactMessageId);
}
