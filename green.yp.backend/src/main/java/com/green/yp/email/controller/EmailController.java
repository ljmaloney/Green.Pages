package com.green.yp.email.controller;

import com.green.yp.api.apitype.email.EmailValidationRequest;
import com.green.yp.email.service.EmailValidationService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("email")
@Tag(name = "Controller supporting email and email addresses including validation token")
public class EmailController {

  private final EmailValidationService validationService;

  public EmailController(EmailValidationService validationService) {
    this.validationService = validationService;
  }

  @ApiResponse(
      responseCode = org.apache.hc.core5.http.HttpStatus.SC_NO_CONTENT + "",
      description = "Validates email address using a token")
  @PostMapping(path = "validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void validateEmail(
      @RequestBody @Valid EmailValidationRequest validationRequest, HttpServletRequest request) {
    validationService.validateEmail(validationRequest, RequestUtil.getRequestIP(request));
  }
}
