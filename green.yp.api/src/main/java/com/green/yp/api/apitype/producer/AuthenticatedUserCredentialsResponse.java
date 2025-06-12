package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AuthenticatedUserCredentialsResponse(
        @NotNull @NonNull String userName,
        @NonNull @NotBlank String firstName,
        @NonNull @NotBlank String lastName,
        String externalAuthorizationServiceRef,
        UUID resetToken,
        OffsetDateTime resetTokenTimeout,
        String emailAddress) {}
