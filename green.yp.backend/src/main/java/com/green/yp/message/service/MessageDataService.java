package com.green.yp.message.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.message.data.repository.ContactMessageRepository;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.ContactMapper;
import com.green.yp.message.mapper.MessageMapper;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageDataService {
  private final MessageRepository messageRepository;
  private final ContactMessageRepository repository;
  private final ContactMapper mapper;
  private final MessageMapper messageMapper;

  public MessageDataService(MessageRepository messageRepository, ContactMessageRepository repository, ContactMapper mapper, MessageMapper messageMapper) {
      this.messageRepository = messageRepository;
      this.repository = repository;
    this.mapper = mapper;
      this.messageMapper = messageMapper;
  }

  public ContactMessageResponse createContactMessage(
      ContactMessageRequest request, String emailAddress, String requestIP) {
    log.info("Creating new generic contact message for {}", emailAddress);
    var contactMessage = mapper.toEntity(request, requestIP);
    contactMessage.setAddresseeName(emailAddress);
    contactMessage.setSourceIpAddress(requestIP);
    contactMessage.setSmsEmailType("email");
    contactMessage.setDestination(emailAddress);

    return mapper.toDto(repository.saveAndFlush(contactMessage));
  }

  public List<ContactMessageResponse> getMessages(
          @NotNull @NonNull LocalDate startDate,
          @NotNull @NonNull LocalDate endDate,
          @NotNull @NonNull ContactMessageRequestType requestType,
          AuthenticatedUser authenticatedUser) {
    log.info("Getting contact messages between {} and {} for {}", startDate, endDate, requestType);

    OffsetDateTime startDateTime = OffsetDateTime.of(startDate, LocalTime.MIDNIGHT, ZoneOffset.UTC);
    OffsetDateTime endDateTime = OffsetDateTime.of(endDate, LocalTime.MIDNIGHT, ZoneOffset.UTC);


    return messageRepository.findContactMessages(startDateTime, endDateTime, requestType)
            .stream()
        .map(messageMapper::toDto)
        .toList();
  }

  public List<ContactMessageResponse> getSubscriberMessages(String startDate, String endDate, ContactMessageRequestType requestType, AuthenticatedUser authenticatedUser) {
    return null;
  }
}
