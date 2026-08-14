package com.green.yp.message.service.impl;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.contract.ClassifiedContract;
import com.green.yp.message.data.model.Message;
import com.green.yp.message.data.model.MessageMeta;
import com.green.yp.message.data.repository.MessageMetaRepository;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.MessageMapper;
import com.green.yp.message.service.EmailService;
import com.green.yp.message.service.MessageSendService;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("classifiedMessage")
public class ClassifiedMessageImpl implements MessageSendService {
  @Value("${greenyp.classified.baseUrl}")
  private String classifiedUrl;

  private final MessageRepository repository;
  private final MessageMetaRepository metaRepository;
  private final MessageMapper mapper;
  private final ClassifiedContract classifiedContract;
  private final EmailService emailService;

  public ClassifiedMessageImpl(
          MessageRepository repository,
          MessageMetaRepository metaRepository,
          MessageMapper mapper,
          ClassifiedContract classifiedContract,
          EmailService emailService) {
    this.repository = repository;
      this.metaRepository = metaRepository;
      this.mapper = mapper;
    this.classifiedContract = classifiedContract;
    this.emailService = emailService;
  }

  @Override
  public ContactMessageResponse createContactMessage(
      ContactMessageRequest request, String requestIP) {
    log.info(
            "Creating new classified ad -> customer contact message for classified id {}",
            request.classifiedRequest().classifiedId());

    var classified =
            classifiedContract.findClassifiedAd(request.classifiedRequest().classifiedId());

    MessageMeta meta = mapper.toEntity(request, classified);
    String messageDescription = String.join(" || ", classified.classified().title(), classified.classified().description());
    meta.setMessageDescription(messageDescription.substring(0,255));

    Message message = mapper.toMessageEntity(request, classified);
    meta.getMessages().add(message);
    message.setMeta(meta);
    message.setSourceIpAddress(requestIP);

    var savedMessage = metaRepository.saveAndFlush(meta);
    return mapper.toResponse(savedMessage, savedMessage.getMessages().getFirst());
  }

  @Override
  public void sendMessage(UUID messageId) {
    log.info("Sending email for classified ad : contactMessageId {}", messageId);

    var message =
        repository
            .findById(messageId)
            .orElseThrow(
                () -> {
                  log.error("No contact message found for id {}", messageId);
                  return new IllegalStateException("No contact message found for messageId"+ messageId);
                });

    var classified = classifiedContract.findClassifiedAd(message.getMeta().getSourceRef());

    var params = createMessageParams(classified, message);
    params.put("timestamp", OffsetDateTime.now());

    emailService.sendEmailAsync(
        EmailTemplateType.CLASSIFIED_CONTACT_INFO,
        Collections.singletonList(classified.customer().emailAddress()),
        EmailTemplateType.CLASSIFIED_CONTACT_INFO.formatSubject(classified.classified().title()),
        () -> params);

    message.setMessageSentDate(OffsetDateTime.now());
    repository.saveAndFlush(message);
  }

  private @NonNull HashMap<String, Object> createMessageParams(ClassifiedAdCustomerResponse classified, Message message) {
    var directLink =
        String.format("%s/classifieds/%s", classifiedUrl, classified.classified().classifiedId());

    var params =
        new HashMap<String, Object>(
            Map.of(
                "firstName", classified.customer().firstName(),
                "lastName", classified.customer().lastName(),
                "link", directLink,
                "classifiedTitle", classified.classified().title(),
                "requestorName", message.getMeta().getRequestorName(),
                "messageSubject", message.getMeta().getSubject(),
                "message", message.getMessage(),
                "contactEmail", message.getFromEmail(),
                "contactPhone", message.getFromPhone(),
                "ipAddress", message.getSourceIpAddress()));
    return params;
  }
}
