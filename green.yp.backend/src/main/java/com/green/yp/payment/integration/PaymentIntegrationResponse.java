package com.green.yp.payment.integration;

import java.util.UUID;

public record PaymentIntegrationResponse(
    UUID acquirerReferenceNumber,
    String avsErrorResponseCode,
    String avsPostalCodeResponseCode,
    String avsStreetAddressResponseCode,
    String cvvResponseCode,
    String responseCode,
    String responseText) {
  public boolean isFailed() {
    return false;
  }
}
