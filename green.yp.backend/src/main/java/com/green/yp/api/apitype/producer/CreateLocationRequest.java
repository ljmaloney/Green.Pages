package com.green.yp.api.apitype.producer;

import java.io.Serializable;
import java.util.Optional;

public record CreateLocationRequest(LocationRequest locationRequest,
                                    ProducerContactRequest contactRequest) implements Serializable {}
