package com.green.yp.message.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.ProducerContactContract;
import com.green.yp.api.contract.ProducerContract;
import com.green.yp.message.data.model.MessageMeta;
import com.green.yp.message.data.repository.MessageMetaRepository;
import com.green.yp.message.data.repository.MessageRepository;
import com.green.yp.message.mapper.MessageMapper;
import com.green.yp.message.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

@Slf4j
public abstract class AbstractProducerMessage implements MessageSendService {

    private final ProducerContract producerContract;
    private final ProducerContactContract contactContract;
    private final MessageMapper mapper;
    private final MessageMetaRepository metaRepository;

    AbstractProducerMessage(ProducerContract producerContract,
                            ProducerContactContract contactContract,
                            MessageMapper mapper,
                            MessageMetaRepository metaRepository)
    {
        this.producerContract = producerContract;
        this.contactContract = contactContract;
        this.mapper = mapper;
        this.metaRepository = metaRepository;
    }

    public Optional<ContactMessageResponse> createContactMessage(ContactMessageRequest request,
                                                                 String requestIP) {
        log.info(
                "Creating new pro -> customer contact message for producer id {} locationId {}",
                request.leadContactRequest().producerId(),
                request.leadContactRequest().locationId());

        var producerProfile =
                producerContract.getProducerProfile(request.leadContactRequest().locationId());

        var messageMeta = createMessage(request, producerProfile, requestIP);
        var message = messageMeta.getMessages().getFirst();
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
        return Optional.of(mapper.toResponse(savedMessage, savedMessage.getMessages().getFirst()));
    }

    abstract MessageMeta createMessage(ContactMessageRequest contactMessageRequest,
                                       ProducerProfileResponse producerProfile,
                                       String requestIP);

}
