package com.green.yp.email.service;

import com.green.yp.api.apitype.email.EmailValidationRequest;
import com.green.yp.api.apitype.email.EmailValidationResponse;
import com.green.yp.api.apitype.email.EmailValidationStatusType;
import com.green.yp.email.data.model.EmailValidation;
import com.green.yp.email.data.repository.EmailValidationRepository;
import com.green.yp.email.mapper.EmailMapper;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.util.TokenUtils;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailValidationService {
  private final EmailValidationRepository repository;
  private final EmailMapper mapper;

  public EmailValidationService(EmailValidationRepository repository, EmailMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public void validateEmail(@Valid EmailValidationRequest validationRequest, String requestIP) {
    log.info(
        "Validating email address {} from ip address {} for {}",
        validationRequest.emailAddress(),
        requestIP,
        validationRequest.externRef());

    repository
        .findByExternRefAndEmailAddress(
            validationRequest.externRef(), validationRequest.emailAddress())
        .ifPresentOrElse(
            emailValidation -> {
              if (StringUtils.equals(emailValidation.getEmailToken(), validationRequest.token())) {
                log.warn(
                    "Email address {} validation tokens do not match from ipAddress {} ",
                    validationRequest.emailAddress(),
                    requestIP);
                throw new PreconditionFailedException("Email address validation failed");
              }
              emailValidation.setEmailValidationDate(OffsetDateTime.now());
              emailValidation.setIpAddress(requestIP);
              emailValidation.setValidationStatus(EmailValidationStatusType.VALIDATED);
            },
            () -> {
              log.warn(
                  "Email address {} not found for {}",
                  validationRequest.emailAddress(),
                  validationRequest.externRef());
              throw new PreconditionFailedException("Email address not found for reference");
            });
  }

  public EmailValidationResponse findValidatedEmail(String externRef, String emailAddress) {
    log.info("Returning email address {} for externRef {}", emailAddress, externRef);
    return repository
        .findByEmailAddress(emailAddress)
        .map(mapper::fromEntity)
        .or(
            () -> {
              EmailValidation emailValidation = new EmailValidation();
              emailValidation.setExternRef(externRef);
              emailValidation.setValidationStatus(EmailValidationStatusType.NOT_VALIDATED);
              emailValidation.setEmailToken(TokenUtils.generateCode(8));
              emailValidation.setEmailAddress(emailAddress);
              return Optional.of(mapper.fromEntity(repository.saveAndFlush(emailValidation)));
            })
        .get();
  }

    public void deleteValidation(String externRef) {
      log.info("Delete email validation record for {}", externRef);
      repository.deleteByExternRef(externRef);
    }
}
