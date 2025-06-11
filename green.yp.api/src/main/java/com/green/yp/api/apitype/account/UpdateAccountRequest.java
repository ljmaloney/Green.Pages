package com.green.yp.api.apitype.account;

import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerRequest;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

public record UpdateAccountRequest(
    @NotNull @NonNull UUID producerId,
    ProducerRequest producerRequest,
    ProducerContactRequest primaryContact,
    LocationRequest primaryLocation,
    UserCredentialsRequest masterUserCredentials) {}
