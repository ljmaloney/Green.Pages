package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.ProducerContactContract;
import com.green.yp.api.contract.ProducerContract;
import com.green.yp.email.data.repository.ContactMessageRepository;
import com.green.yp.email.mapper.ContactMapper;
import com.green.yp.email.service.MessageSendService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service("genericMessage")
public class GenericMessageImpl implements MessageSendService {

  private final ProducerContract producerContract;
  private final ProducerContactContract contactContract;
  private final ContactMapper mapper;
  private final ContactMessageRepository repository;

  public GenericMessageImpl(
      ProducerContract producerContract,
      ProducerContactContract producerContactContract,
      ContactMapper mapper,
      ContactMessageRepository repository) {
    this.producerContract = producerContract;
    this.contactContract = producerContactContract;
    this.mapper = mapper;
    this.repository = repository;
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

    var message = mapper.toEntity(request, producerProfile, requestIP);

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
            });
    message.setSmsEmailType("email");
    return mapper.toDto(repository.saveAndFlush(message));
  }

  @Override
  public void sendMessage(UUID contactMessageId) {}
}
