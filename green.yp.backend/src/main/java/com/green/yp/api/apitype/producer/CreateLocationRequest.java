package com.green.yp.api.apitype.producer;

import java.io.Serializable;

public record CreateLocationRequest(
    LocationRequest locationRequest, ProducerContactRequest contactRequest)
    implements Serializable {}
