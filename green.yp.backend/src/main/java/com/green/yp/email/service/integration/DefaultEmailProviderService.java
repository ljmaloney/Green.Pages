package com.green.yp.email.service.integration;

import com.green.yp.api.apitype.enumeration.EmailTemplateName;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultEmailProviderService implements EmailProviderInterface {
    @Override
    public Optional<Object> sendEmail(
            EmailTemplateName templateName, String[] toEmailAddress, Object params) {
        log.info("Sending email {} to {} with Parameters [{}]", templateName, toEmailAddress, params);
        return Optional.empty();
    }
}
