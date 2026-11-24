package com.green.yp.message.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.producer.ProducerProfileResponse;

import com.green.yp.api.contract.ProducerContactContract;
import com.green.yp.api.contract.ProducerContract;
import com.green.yp.message.data.model.MessageMeta;
import com.green.yp.message.data.repository.MessageMetaRepository;
import com.green.yp.message.mapper.MessageMapper;
import com.green.yp.message.service.MessageSendService;


import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("genericMessage")
public class ProducerGenericMessageImpl extends AbstractProducerMessage implements MessageSendService {

  private MessageMapper mapper;

  public ProducerGenericMessageImpl(
          ProducerContract producerContract,
          ProducerContactContract producerContactContract,
          MessageMapper mapper,
          MessageMetaRepository metaRepository) {
    super(producerContract, producerContactContract, mapper, metaRepository);
    this.mapper = mapper;
  }

  @Override
  MessageMeta createMessage(ContactMessageRequest contactMessageRequest, ProducerProfileResponse producerProfile, String requestIP) {
    var messageMeta = mapper.toEntity(contactMessageRequest, producerProfile, requestIP);
    var message = mapper.toMessageEntity(contactMessageRequest, producerProfile, requestIP);
    message.setMeta(messageMeta);
    messageMeta.addMessage(message);
    messageMeta.setMessageDescription(producerProfile.businessName());
    return messageMeta;
  }

  @Override
  public void sendMessage(UUID contactMessageId) {}
}
