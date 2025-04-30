package com.green.yp.email.service;

import com.green.yp.api.apitype.enumeration.EmailTemplateName;
import com.green.yp.email.service.integration.EmailProviderInterface;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailProviderInterface emailProvider;

    public EmailService(EmailProviderInterface emailProvider) {
        this.emailProvider = emailProvider;
    }

    @Async("sendEmailThreadPool")
    public void sendEmail(EmailTemplateName emailTemplateName, Object object, String... adminEmails) {
        // TODO: prepare template params from input data

        emailProvider.sendEmail(emailTemplateName, adminEmails, null);
    }
}
