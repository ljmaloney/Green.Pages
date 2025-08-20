package com.green.yp.api.apitype.producer;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProducerUserResponse(
    UUID producerId,
    UUID producerContactId,
    UUID credentialsId,
    OffsetDateTime createdDate,
    OffsetDateTime lastUpdateDate,
    String firstName,
    String lastName,
    String emailAddress) {}
