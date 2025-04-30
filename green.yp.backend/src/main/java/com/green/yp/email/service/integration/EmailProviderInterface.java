package com.green.yp.email.service.integration;

import com.green.yp.api.apitype.enumeration.EmailTemplateName;
import java.util.Optional;

public interface EmailProviderInterface {
  Optional<Object> sendEmail(EmailTemplateName templateName, String[] emailAddress, Object params);
}
