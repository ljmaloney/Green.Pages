package com.green.yp.api.apitype.contact;

import lombok.Getter;

@Getter
public enum ContactMessageRequestType {
    CLASSIFIED_AD_EMAIL("Customer contacting classified ad customer" ,"classifiedMessage"),
    SUBSCRIBER_INFO_TYPE("Subscriber contact request", "noEmail"),
    SUBSCRIBER_SUPPORT_TYPE("Subscriber support contact request", "supportMessage"),
    PRODUCER_GENERIC_TYPE("Generic producer contact request", "genericMessage"),
    PRODUCER_PRODUCT_TYPE("Contact regarding specific product" ,"productMessage"),
    PRODUCER_SERVICE_TYPE("Contact regarding specific service", "serviceMessage");

    private final String description;
    private final String emailSender;
    ContactMessageRequestType(String description,  String emailSender) {
        this.description = description;
        this.emailSender = emailSender;
    }
}
