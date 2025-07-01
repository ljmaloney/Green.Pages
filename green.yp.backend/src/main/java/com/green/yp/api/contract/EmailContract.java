package com.green.yp.api.contract;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.enumeration.EmailTemplateName;
import com.green.yp.api.apitype.producer.ProducerResponse;
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
      EmailTemplateName emailTemplateName, ProducerResponse producer, String... adminEmails) {
    emailService.sendEmail(emailTemplateName, producer, adminEmails);
  }

  public void sendEmail(
      EmailTemplateName emailTemplateName, ProducerContact contact, String emailAddress) {
    emailService.sendEmail(emailTemplateName, contact, emailAddress);
  }

  public void sendEmail(EmailTemplateName emailTemplateName, ContactMessageRequest contactMessageRequest, String emailAddress){
    emailService.sendEmail(emailTemplateName, contactMessageRequest, emailAddress);
  }
}
