package com.green.yp.api.apitype.payment;

import java.util.UUID;

public record PaymentCustomerResponse(String externCustRef,
                                      String firstName,
                                      String lastName,
                                      UUID idempotencyId) {}
