package com.green.yp.api.apitype.producer;

import java.io.Serializable;
import java.util.Optional;

public record CreateLocationRequest(LocationRequest locationRequest,
                                    Optional<ProducerContactRequest> contactRequest) implements Serializable {}
