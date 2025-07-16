package com.green.yp.api.apitype.payment;

import com.green.yp.payment.data.json.Card;
import java.util.UUID;

public record PaymentSavedCardResponse(String cardRef,
                                       Card card,
                                       UUID paymentMethodId) {}
