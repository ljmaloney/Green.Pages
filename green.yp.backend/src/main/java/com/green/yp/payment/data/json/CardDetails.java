package com.green.yp.payment.data.json;

import java.util.List;
import java.util.Map;

public record CardDetails(
    String status,
    Card card,
    String entryMethod,
    String cvvStatus,
    String avsStatus,
    String authResultCode,
    String applicationIdentifier,
    String applicationName,
    String applicationCryptogram,
    String verificationMethod,
    String verificationResults,
    String statementDescription,
    CardPaymentTimeline cardPaymentTimeline,
    Boolean refundRequiresCardPresence,
    List<CardError> errors,
    Map<String, Object> additionalProperties) {}
