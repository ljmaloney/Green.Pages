package com.green.yp.api.apitype.contact;

import lombok.Getter;

@Getter
public enum ContactMessageRequestType {
    CLASSIFIED_AD_EMAIL("Customer contacting classified ad customer"),
    SUBSCRIBER_INFO_TYPE("Subscriber contact request"),
    SUBSCRIBER_SUPPORT_TYPE("Subscriber support contact request"),
    PRODUCER_GENERIC_TYPE("Generic producer contact request"),
    PRODUCER_PRODUCT_TYPE("Contact regarding specific product"),
    PRODUCER_SERVICE_TYPE("Contact regarding specific service");

    private String description;
    private ContactMessageRequestType(String description){
        this.description = description;
    }
}
