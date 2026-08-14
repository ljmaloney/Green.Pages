package com.green.yp.message.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;

import java.util.List;
import java.util.Map;

import com.green.yp.exception.BusinessException;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

  private final Map<String, MessageSendService> senders;
  private MessageRepository messageRepository;
  private MessageMapper mapper;

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

  public List<ContactMessageResponse> getContactMessages(String emailAddress, String messageRef) {
    if (StringUtils.isBlank(emailAddress) && StringUtils.isBlank(messageRef)){
      log.info("No email address or message ref provided");
      throw new BusinessException("No email address or reference provided",
              HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
    }
    return mapper.toDto(messageRepository.findMessages(emailAddress, messageRef));
  }
}
