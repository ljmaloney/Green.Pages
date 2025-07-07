package com.green.yp.api.apitype.contact;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ProducerContactRequestType {
    GENERAL_REQUEST("General Contact Request"),
    PRODUCT_REQUEST("Product Contact Request"),
    SERVICE_REQUEST("Service Contact Request");
    private final String requestType;
    private ProducerContactRequestType(String name){
        this.requestType = name;
    }
}
