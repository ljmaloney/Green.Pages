package com.green.yp.email.service;

import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import freemarker.template.Template;
import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  private final Configuration freemarkerConfig;

  public EmailService(JavaMailSender mailSender, Configuration freemarkerConfig) {
    this.mailSender = mailSender;
    this.freemarkerConfig = freemarkerConfig;
  }

  @Async("sendEmailThreadPool")
  public void sendEmailAsync(EmailTemplateType emailTemplate, String[] to, String subject,
                             Supplier<Map<String,Object>> mappingSupplier) throws Exception {
    MimeMessage message = mailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setFrom("no-reply@greenyp.com");

    var model = mappingSupplier.get();
    model.put("subject", StringUtils.isBlank(subject) ? emailTemplate.getSubjectFormat() : subject);

    Template template = freemarkerConfig.getTemplate(emailTemplate.getTemplateFileName());
    StringWriter out = new StringWriter();
    template.process(model, out);

    helper.setText(out.toString(), true); // true = HTML

    mailSender.send(message);
  }

  public void sendEmail(EmailTemplateType emailTemplateType, Object object, String ...adminEmails) {
  }
}
