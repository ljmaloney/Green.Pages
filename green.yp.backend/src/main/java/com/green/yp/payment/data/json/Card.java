package com.green.yp.payment.data.json;

import java.util.Map;

public record Card(
    String id,
    String cardBrand,
    String last4,
    Long expMonth,
    Long expYear,
    String cardholderName,
    String fingerprint,
    String customerId,
    String merchantId,
    String referenceId,
    Boolean enabled,
    String cardType,
    String prepaidType,
    String bin,
    Long version,
    String issuerAlert,
    String issuerAlertAt,
    Boolean hsaFsa,
    Map<String, Object> additionalProperties) {}
