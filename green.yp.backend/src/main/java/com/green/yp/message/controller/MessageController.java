package com.green.yp.message.controller;

import com.green.yp.api.message.MessageMetaResponse;
import com.green.yp.api.message.MessageResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.message.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping(path="meta", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<MessageMetaResponse>> getContactMessages(@RequestParam(name="emailAddress", required = false) String emailAddress,
                                                                     @RequestParam(name="messageRef", required = false) UUID messageRef) {
        return new ResponseApi<>(messageService.getContactMessages(emailAddress, messageRef), null);
    }

    @GetMapping(path="meta/{messageMetaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<List<MessageResponse>> getMessages(@PathVariable("messageMetaId") UUID messageMetaId,
                                                          @RequestHeader("x-email-addr") String emailAddress,
                                                          @RequestHeader("x-msg-profile-ref") UUID messageRef){
       return new ResponseApi<>(messageService.getMessages(messageMetaId, emailAddress, messageRef), null);
    }

    @DeleteMapping(path = "/{messageId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void archiveMessage(@PathVariable("messageId") UUID messageId,
                               @RequestHeader("x-email-addr") String emailAddress,
                               @RequestHeader("x-msg-profile-ref") UUID messageRef){
        messageService.archiveMessage(messageId, emailAddress, messageRef);
    }
}
