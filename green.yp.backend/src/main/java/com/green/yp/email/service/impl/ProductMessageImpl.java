package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.service.MessageSendService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service("productMessage")
public class ProductMessageImpl implements MessageSendService {
  @Override
  public ContactMessageResponse createContactMessage(
      ContactMessageRequest contactMessageRequest, String requestIP) {
    return null;
  }

  @Override
  public void sendMessage(UUID contactMessageId) {}
}
