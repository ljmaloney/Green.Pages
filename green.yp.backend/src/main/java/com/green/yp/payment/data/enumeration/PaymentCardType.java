package com.green.yp.payment.data.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum PaymentCardType {
    MC("MasterCard"),
    VISA("Visa"),
    DISC("Discover"),
    AMEX("American Express");

    private final String cardBrandName;

    PaymentCardType(String brandName) {
        this.cardBrandName = brandName;
    }

    public static PaymentCardType findValueByCardBrandName(String brandName) {
        return Arrays.stream(PaymentCardType.values())
                .filter(value -> value.getCardBrandName().equalsIgnoreCase(brandName))
                .findFirst()
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        String.format("Debit card type not found for brand name: %s", brandName)));
    }
}
