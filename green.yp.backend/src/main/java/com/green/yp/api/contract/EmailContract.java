package com.green.yp.api.contract;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.email.service.EmailService;
import com.green.yp.producer.data.model.ProducerContact;
import org.springframework.stereotype.Service;

@Service
public class EmailContract {

  private final EmailService emailService;

  public EmailContract(EmailService emailService) {
    this.emailService = emailService;
  }

  public void sendEmail(
          EmailTemplateType emailTemplateType, ProducerContact contact, String emailAddress)  {
    emailService.sendEmail(emailTemplateType, contact, emailAddress);
  }

  public void sendEmail(EmailTemplateType emailTemplateType, ContactMessageRequest contactMessageRequest, String emailAddress) {
    emailService.sendEmail(emailTemplateType, contactMessageRequest, emailAddress);
  }
}
