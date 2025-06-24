package com.green.yp.api.apitype.enumeration;

import lombok.Getter;

@Getter
public enum ContactRequestType {
    SUBSCRIBER_INFO_TYPE("Subscriber contact request"),
    SUBSCRIBER_SUPPORT_TYPE("Subscriber support contact request"),
    PRODUCER_GENERIC_TYPE("Generic producer contact request"),
    PRODUCER_PRODUCT_TYPE("Contact regarding specific product"),
    PRODUCER_SERVICE_TYPE("Contact regarding specific service");

    private String description;
    private ContactRequestType(String description){
        this.description = description;
    }
}
