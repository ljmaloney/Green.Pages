package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.enumeration.SubscriptionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerSubscriptionResponse(
    UUID subscriptionId,
    UUID producerSubscriptionId,
    String displayName,
    String shortDescription,
    InvoiceCycleType invoiceCycleType,
    SubscriptionType subscriptionType,
    LocalDate nextInvoiceDate,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal subscriptionAmount) {}
