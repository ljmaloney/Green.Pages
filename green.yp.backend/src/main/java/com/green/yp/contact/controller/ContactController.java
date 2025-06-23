package com.green.yp.contact.controller;

import com.green.yp.api.apitype.contact.ContactRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("contact")
@Validated
@Tag(name = "Endpoint for submitting contact request / sending contact messages")
@RestController
public class ContactController {

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void processContactRequest(@RequestBody ContactRequest contactRequest){

    }
}
