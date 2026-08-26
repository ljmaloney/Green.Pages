package com.green.yp.message.service;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.green.yp.api.message.MessageMetaResponse;
import com.green.yp.api.message.MessageResponse;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.message.data.model.AppMessageRecord;
import com.green.yp.message.data.model.Message;
import com.green.yp.message.data.model.MessageStatus;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.MessageMapper;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public List<MessageMetaResponse> getContactMessages(@NonNull String emailAddress, UUID messageRef) {
    if (StringUtils.isBlank(emailAddress) && messageRef == null){
      log.info("No email address or message ref provided");
      throw new BusinessException("No email address or reference provided",
              HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
    }
    List<AppMessageRecord> messages = messageRepository.findMessages(emailAddress,messageRef);
    return mapper.toMetaResponse(messages);
  }

  public List<MessageResponse> getMessages(@NotNull @NonNull UUID messageMetaId,
                                           String emailAddress,
                                           UUID messageRef) {
    if (StringUtils.isBlank(emailAddress) && messageRef == null){
      log.info("No email address or message ref provided");
      throw new BusinessException("No email address or reference provided",
              HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
    }
    log.info("Getting messages for {}", messageMetaId);
    List<Message> messages = messageRepository.findMessages(messageMetaId, emailAddress, messageRef);
    if ( CollectionUtils.isEmpty(messages)){
      log.warn("No messages found for {}", messageMetaId);
      return new ArrayList<MessageResponse>();
    }
    return mapper.toResponse(messages);
  }

  @Transactional
  void markMessageRead(UUID messageId, String emailAddress, UUID messageRef){
//    if (StringUtils.isBlank(emailAddress) && messageRef == null){
//      log.info("No email address or message ref provided");
//      throw new BusinessException("No email address or reference provided",
//              HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
//    }
//    messageRepository.
  }

  @Transactional
  public void archiveMessage(UUID messageId, String emailAddress, UUID messageRef) {
    if (StringUtils.isBlank(emailAddress) && messageRef == null){
      log.info("No email address or message ref provided");
      throw new BusinessException("No email address or reference provided",
              HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
    }
    messageRepository.archiveMessage(messageId, emailAddress, messageRef, MessageStatus.ARCHIVED);
  }
}
