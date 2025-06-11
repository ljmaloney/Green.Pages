package com.green.yp.api.apitype.account;

import com.green.yp.api.apitype.producer.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public record CreateAccountRequest(
    @NotNull @NonNull CreateProducerRequest producerRequest,
    @NotNull @NonNull ProducerContactRequest primaryContact,
    @NotNull @NonNull LocationRequest primaryLocation,
    @NotNull @NonNull UserCredentialsRequest masterUserCredentials) {}
