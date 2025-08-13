package com.green.yp.email.service.impl;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.contract.ClassifiedContract;
import com.green.yp.email.data.repository.ContactMessageRepository;
import com.green.yp.email.mapper.ContactMapper;
import com.green.yp.email.service.EmailService;
import com.green.yp.email.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Qualifier("classifiedMessage")
public class ClassifiedMessageImpl implements MessageSendService {
    @Value("${greenyp.classified.baseUrl}")
      private String classifiedUrl;

    private final ContactMessageRepository repository;
    private final ContactMapper mapper;
    private final ClassifiedContract classifiedContract;
    private final EmailService emailService;

    public ClassifiedMessageImpl(ContactMessageRepository repository,
                                 ContactMapper mapper,
                                 ClassifiedContract classifiedContract, EmailService emailService) {
        this.repository = repository;
        this.mapper = mapper;
        this.classifiedContract = classifiedContract;
        this.emailService = emailService;
    }

    @Override
    public ContactMessageResponse createContactMessage(ContactMessageRequest request, String requestIP) {
        log.info("Creating new classified ad -> customer contact message for classified id {}",
                request.classifiedRequest().classifiedId());

        var classified = classifiedContract.findClassifiedAd(request.classifiedRequest().classifiedId());

        var contactMessage = mapper.toEntity(request, classified, requestIP);
        contactMessage.setAddresseeName(String.join(" ", classified.customer().firstName(),classified.customer().lastName()));
        contactMessage.setSmsEmailType("email");
        return mapper.toDto(repository.saveAndFlush(contactMessage));
    }

    @Override
    public void sendMessage(UUID contactMessageId) {
        log.info("Sending email for classified ad : contactMessageId {}", contactMessageId);

        var message = repository.findById(contactMessageId)
                .orElseThrow(() -> {
                    log.error("No contact message found for id {}", contactMessageId);
                    return new IllegalStateException("No classified ad with id: " + contactMessageId);
                });

        var classified = classifiedContract.findClassifiedAd(message.getClassifiedId());

        var directLink = String.format("%s/classifieds/%s", classifiedUrl, classified.classified().classifiedId());

        emailService.sendEmailAsync(EmailTemplateType.CLASSIFIED_CONTACT_INFO,
                Collections.singletonList(message.getDestination()),
                EmailTemplateType.CLASSIFIED_CONTACT_INFO.formatSubject(classified.classified().title()),
                () -> Map.of(
                        "firstName", classified.customer().firstName(),
                        "lastName", classified.customer().lastName(),
                        "directLink", directLink,
                        "classifiedTitle", classified.classified().title(),
                        "requestorName", message.getRequestorName(),
                        "subject", message.getTitle(),
                        "message", message.getMessage(),
                        "contactEmail", message.getFromEmail(),
                        "contactPhone", message.getFromPhone(),
                        "ipAddress", message.getSourceIpAddress()));

        message.setMessageSentDate(OffsetDateTime.now());
    }
}
