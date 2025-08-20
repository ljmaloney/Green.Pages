package com.green.yp.api.apitype.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailValidationRequest(
    @NotNull @NotNull String externRef,
    @Email String emailAddress,
    @NotNull @NotNull String token) {}
