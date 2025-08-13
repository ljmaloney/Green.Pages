package com.green.yp.email.controller;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.email.service.MessageService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/email/contact")
@Validated
@Tag(name = "Endpoint for submitting contact request / sending contact messages")
@RestController
public class ContactController {

  private final MessageService service;

  public ContactController(MessageService service) {
    this.service = service;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void processContactRequest(@RequestBody ContactMessageRequest contactMessageRequest) {
    service.sendMessage(contactMessageRequest, RequestUtil.getRequestIP());
  }
}
