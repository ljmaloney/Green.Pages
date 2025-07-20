package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
  default BigInteger convertToCents(BigDecimal amount) {
    return amount
        .setScale(2, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .toBigInteger();
  }

  PaymentResponse processPayment(
      PaymentRequest paymentRequest, UUID paymentTransactionId, Optional<String> customerRef);

  PaymentCustomerResponse createCustomer(PaymentMethodRequest methodRequest, UUID paymentMethodId);

  PaymentSavedCardResponse createCardOnFile(
      PaymentMethodRequest methodRequest, String externCustId, UUID paymentMethodId);

  PaymentCustomerResponse updateCustomer(
      PaymentMethodRequest methodRequest, String externCustId, UUID paymentMethodId);

  void deactivateExistingCard(String cardRef);

  void deleteCustomer(String externCustomerRef);
}
