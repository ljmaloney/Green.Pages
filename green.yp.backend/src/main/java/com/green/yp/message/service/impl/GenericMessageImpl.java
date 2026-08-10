package com.green.yp.message.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.ProducerContactContract;
import com.green.yp.api.contract.ProducerContract;
import com.green.yp.message.data.repository.MessageMetaRepository;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.MessageMapper;
import com.green.yp.message.service.MessageSendService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service("genericMessage")
public class GenericMessageImpl implements MessageSendService {

  private final ProducerContract producerContract;
  private final ProducerContactContract contactContract;
  private final MessageMapper mapper;
  private final MessageRepository repository;
  private final MessageMetaRepository metaRepository;

  public GenericMessageImpl(
          ProducerContract producerContract,
          ProducerContactContract producerContactContract,
          MessageMapper mapper,
          MessageRepository repository,
          MessageMetaRepository metaRepository) {
    this.producerContract = producerContract;
    this.contactContract = producerContactContract;
    this.mapper = mapper;
    this.repository = repository;
      this.metaRepository = metaRepository;
  }

  @Override
  public ContactMessageResponse createContactMessage(
      ContactMessageRequest request, String requestIP) {
    log.info(
        "Creating new pro -> customer contact message for producer id {} locationId {}",
        request.leadContactRequest().producerId(),
        request.leadContactRequest().locationId());

    var producerProfile =
        producerContract.getProducerProfile(request.leadContactRequest().locationId());

    var messageMeta = mapper.toEntity(request, producerProfile, requestIP);
    var message = mapper.toMessageEntity(request, producerProfile, requestIP);
    message.setMeta(messageMeta);
    messageMeta.getMessages().add(message);

    contactContract
        .findContacts(
            request.leadContactRequest().producerId(), request.leadContactRequest().locationId())
        .stream()
        .filter(c -> c.producerContactType() == ProducerContactType.PRIMARY)
        .findFirst()
        .ifPresent(
            c -> {
              if (StringUtils.isNotBlank(c.firstName()) && StringUtils.isNotBlank(c.lastName())) {
                message.setAddresseeName(String.join(" ", c.firstName(), c.lastName()));
              } else if (StringUtils.isNotBlank(c.genericContactName())) {
                message.setAddresseeName(c.genericContactName());
              }
              message.setDestination(c.emailAddress());
            });
    message.setSmsEmailType("email");
    var savedMessage = metaRepository.saveAndFlush(messageMeta);
    return mapper.toResponse(savedMessage, savedMessage.getMessages().getFirst());
  }

  @Override
  public void sendMessage(UUID contactMessageId) {}
}
