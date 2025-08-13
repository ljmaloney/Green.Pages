package com.green.yp.email.controller;

import com.green.yp.api.apitype.contact.ContactMessageRequest;
import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.config.security.AuthUser;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.email.service.MessageDataService;
import com.green.yp.email.service.MessageService;
import com.green.yp.security.IsAdmin;
import com.green.yp.util.DateUtils;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequestMapping("/email/contact")
@Validated
@Tag(name = "Endpoint for submitting contact request / sending contact messages")
@RestController
public class ContactController {

  private final MessageService service;
  private final MessageDataService dataService;

  public ContactController(MessageService service, MessageDataService dataService) {
    this.service = service;
      this.dataService = dataService;
  }

  @Operation(summary = "Returns the list of contact messages")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @IsAdmin
  public ResponseApi<List<ContactMessageResponse>> getContactMessages(@RequestParam("startDate") String startDate,
                                                                      @RequestParam("endDate") String endDate,
                                                                      @RequestParam("requestType") ContactMessageRequestType requestType,
                                                                      @Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser) {
      return new ResponseApi<>(dataService.getMessages(DateUtils.parseDate(startDate, LocalDate.class),
              DateUtils.parseDate(endDate, LocalDate.class), requestType), null);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void processContactRequest(@RequestBody @Valid ContactMessageRequest contactMessageRequest,
                                    HttpServletRequest request) {
    service.sendMessage(contactMessageRequest, RequestUtil.getRequestIP(request));
  }
}
