package com.green.yp.api.apitype.payment;

import java.util.UUID;

public record PaymentMethodResponse(String externCustRef,
                                    UUID idempotencyId) {}
