package com.green.yp.api.apitype.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerCredentialsResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(ProducerResponse producer,
                              ProducerLocationResponse primaryLocation,
                              List<ProducerContactResponse> contacts,
                              ProducerCredentialsResponse primaryUserCredentials) {
}
