package com.green.yp.api.apitype.account;

import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerRequest;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public record CreateAccountRequest(
    @NotNull @NonNull ProducerRequest producerRequest,
    @NotNull @NonNull ProducerContactRequest primaryContact,
    @NotNull @NonNull LocationRequest primaryLocation,
    @NotNull @NonNull UserCredentialsRequest masterUserCredentials) {}
