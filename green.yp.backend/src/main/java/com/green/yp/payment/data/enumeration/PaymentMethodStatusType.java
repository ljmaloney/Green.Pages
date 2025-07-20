package com.green.yp.payment.data.enumeration;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethodStatusType {
    TEMP(false),    //new db record
    CUSTOMER_CREATED(true),  //square customer record created
    CCOF_CREATED(true),  //square customer record and card on file
    DISABLED(false);    //disabled record
    boolean active;
    PaymentMethodStatusType(boolean active) {
        this.active = active;
    }
    public static List<PaymentMethodStatusType> activeTypes() {
        return Arrays.stream(PaymentMethodStatusType.values())
                .filter(v -> v.active)
                .toList();
    }
}
