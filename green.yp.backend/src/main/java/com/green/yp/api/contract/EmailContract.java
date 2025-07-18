package com.green.yp.api.contract;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.email.EmailValidationResponse;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.email.service.EmailService;
import com.green.yp.email.service.EmailValidationService;
import com.green.yp.producer.data.model.ProducerContact;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class EmailContract {

  private final EmailService emailService;
  private final EmailValidationService emailValidationService;

  public EmailContract(EmailService emailService, EmailValidationService emailValidationService) {
    this.emailService = emailService;
      this.emailValidationService = emailValidationService;
  }

  public void sendEmail(
          EmailTemplateType emailTemplateType, ProducerContact contact, String emailAddress)  {
    emailService.sendEmail(emailTemplateType, contact, emailAddress);
  }

  public void sendEmail(EmailTemplateType emailTemplateType, ContactMessageRequest contactMessageRequest, String emailAddress) {
    emailService.sendEmail(emailTemplateType, contactMessageRequest, emailAddress);
  }

  public void sendEmail(EmailTemplateType emailTemplateType, List<String> emailsAddress,
                        String subject, @NotNull Supplier<Map<String, Object>> mappingSupplier){
    emailService.sendEmailAsync(emailTemplateType, emailsAddress, subject, mappingSupplier);
  }

  public EmailValidationResponse validateEmail(String externRef, String emailAddress){
    return emailValidationService.findValidatedEmail(externRef, emailAddress);
  }
}
