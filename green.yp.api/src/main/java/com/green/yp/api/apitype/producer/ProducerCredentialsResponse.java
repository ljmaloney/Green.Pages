package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerCredentialsResponse(
    @NotNull @NonNull UUID credentialsId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    @NotNull @NonNull String userName,
    @NonNull @NotBlank String firstName,
    @NonNull @NotBlank String lastName,
    UUID producerId,
    UUID producerContactId,
    @NotNull @NonNull OffsetDateTime lastChangeDate,
    @NotNull @NonNull Boolean adminUser,
    @NotNull @NonNull Boolean enabled,
    String externalAuthorizationServiceRef,
    UUID resetToken,
    OffsetDateTime resetTokenTimeout,
    String emailAddress) {}
