package com.green.yp.message.controller;

import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.message.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/message")
@Validated
@Tag(name = "Endpoint for submitting contact request / sending contact messages")
@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ContactMessageResponse> getContactMessages(@RequestParam(name="emailAddress", required = false) String emailAddress,
                                                           @RequestParam(name="messageRef", required = false) String messageRef) {
        return messageService.getContactMessages(emailAddress, messageRef);
    }
}
