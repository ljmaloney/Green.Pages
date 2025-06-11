package com.green.yp.api.apitype.producer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.UUID;

public record UserCredentialsRequest(
    UUID producerContactId,
    @NonNull @NotBlank String firstName,
    @NonNull @NotBlank String lastName,
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
    @NonNull @NotBlank String businessPhone,
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
    String cellPhone,
    @NonNull @NotBlank String emailAddress,
    @NotNull
        @NonNull
        @NotBlank(message = "Your user name must be between 8 and 150 characters")
        @Size(min = 8, max = 150, message = "Your user name must be between 8 and 150 characters")
        String userName,
    @NotNull
        @NonNull
        @NotBlank(message = "Your password must be between 8 and 20 characters in length")
        @Size(
            min = 8,
            max = 20,
            message = "Your password must be between 8 and 20 characters in length")
        @Pattern(
            regexp = "^(?=.*?[A-Za-z0-9#?!@$%^&*-]).{8,}$",
            message =
                "Your password must contain one uppercase letter, one number, and one special character")
        String credentials) {}
