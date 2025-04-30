package com.green.yp.usps.enumeration;

import java.util.Arrays;

public enum DPVConfirmationType {
    CONFIRMED("Y", "Address was DPV confirmed for both primary and (if present) secondary numbers"),
    CONFIRMED_SECONDARY_MISSING("D","Address was DPV confirmed for the primary number only, and the secondary number information was missing"),
    CONFIRMED_PRIMARY_ONLY("S","Address was DPV confirmed for the primary number only, and the secondary number information was present by not confirmed"),
    NOT_CONFIRMED("N","Both primary and (if present) secondary number information failed to DPV confirm");
    private String code;
    private String description;
    private DPVConfirmationType(String code, String description){
        this.code = code;
        this.description = description;
    }

    public static DPVConfirmationType getConfimationType(String code){
        return Arrays.stream(DPVConfirmationType.values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No DPV Confirmation Type for : "+code));
    }
}
