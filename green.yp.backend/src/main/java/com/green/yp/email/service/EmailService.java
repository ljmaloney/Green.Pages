package com.green.yp.email.service;

import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.exception.SystemException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

  @Value("${green.yp.default.from:no-reply@greenyp.com}")
  private String defaultFromEmail;

  private final JavaMailSender mailSender;

  private final Configuration freemarkerConfig;

  public EmailService(JavaMailSender mailSender, Configuration freemarkerConfig) {
    this.mailSender = mailSender;
    this.freemarkerConfig = freemarkerConfig;
  }

  @Async("sendEmailThreadPool")
  public void sendEmailAsync(
      @NotNull EmailTemplateType emailTemplate,
      @NotNull @Size(min = 1) List<String> toAddressSupplier,
      String subject,
      @NotNull Supplier<Map<String, Object>> mappingSupplier) {
    log.info("Sending {} email to {}" , emailTemplate.getSubjectFormat(), toAddressSupplier);
    try {
      MimeMessage message = mailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(toAddressSupplier.toArray(new String[0]));
      helper.setSubject(subject);
      helper.setFrom(defaultFromEmail);

        var model = new HashMap<String, Object>(mappingSupplier.get());
      model.put(
          "subject", StringUtils.isBlank(subject) ? emailTemplate.getSubjectFormat() : subject);

      Template template = freemarkerConfig.getTemplate(emailTemplate.getTemplateFileName());
      StringWriter out = new StringWriter();
      template.process(model, out);

      helper.setText(out.toString(), true); // true = HTML

      mailSender.send(message);
      log.info("Sent {} message successfully", emailTemplate.getSubjectFormat());
    } catch (Exception e) {
      log.error("Unexpected exception sending email", e);
      throw new SystemException("Unexpected exception sending email", e);
    }
  }

  public void sendEmail(EmailTemplateType emailTemplateType, Object object, String... adminEmails) {
    sendEmailAsync(
        emailTemplateType,
        Arrays.asList(adminEmails),
        emailTemplateType.getSubjectFormat(),
        () -> Map.of("data", object));
  }
}
