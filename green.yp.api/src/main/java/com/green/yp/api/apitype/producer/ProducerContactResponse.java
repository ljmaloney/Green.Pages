package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerContactResponse(
    @NotNull UUID contactId,
    @NotNull OffsetDateTime createDate,
    @NotNull OffsetDateTime lastUpdateDate,
    @NotNull UUID producerId,
    UUID producerLocationId,
    @NotNull ProducerContactType producerContactType,
    @NotNull ProducerDisplayContactType displayContactType,
    String genericContactName,
    String firstName,
    String lastName,
    String title,
    String phoneNumber,
    String cellPhoneNumber,
    Boolean emailConfirmed,
    OffsetDateTime emailConfirmedDate,
    String emailAddress,
    String userName) {}
