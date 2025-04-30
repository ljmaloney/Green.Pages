package com.green.yp.usps.enumeration;

import java.util.Arrays;

/**
 * DPV® Standardized Footnotes - EZ24x7Plus and Mail*STAR are required to express DPV results using USPS standard two character footnotes.
 * Example: AABB
 *
 * Footnotes Reporting CASS™ ZIP+4™ Certification
 *
 * AA – Input address matched to the ZIP+4 file.
 * A1 – Input address not matched to the ZIP+4 file.
 *
 * Footnotes Reporting DPV Validation Observations
 *
 * BB - Matched to DPV (all components).
 * CC - Secondary number not matched (present but invalid).
 * N1 - High-rise address missing secondary number.
 * M1 - Primary number missing.
 * M3 - Primary number invalid.
 * P1 - Input Address RR or HC Box number Missing.
 * P3 - Input Address PO, RR, or HC Box number Invalid.
 * F1 - Input Address Matched to a Military Address.
 * G1 - Input Address Matched to a General Delivery Address.
 * U1- Input Address Matched to a Unique ZIP Code™.
 */
public enum DPVFootnoteType {
    ADDRESS_MATCHED("AA", "Input address matched to the ZIP+4 file"),
    ADDRESS_NOT_MATCHED("A1", "Input address not matched to the ZIP+4 file"),
    DPV_MATCHED_ALL("BB", "Matched to DPV (all components)"),
    SECONDARY_NOT_MATCHED("CC", "Secondary number not matched (present but invalid"),
    HIGH_RISE_MISSING_SECONARD("N1","High-rise address missing secondary number"),
    PRIMARY_NUMBER_MISSING("M1", "Primary number missing"),
    PRIMARY_NUMBER_INVALID("M3", "Primary number invalid"),
    RR_HCBOX_NUMBER_MISSING("P1","Input Address RR or HC Box number Missing"),
    PO_RR_HCBOX_NUMBER_INVALID("P3", "Input Address PO, RR, or HC Box number Invalid"),
    MILITARY_ADDRESS("F1","Input Address Matched to a Military Address"),
    GENERAL_DELIVERY("G1", "Input Address Matched to a General Delivery Address"),
    UNIQUE_ZIP_CODE("U1", "Input Address Matched to a Unique ZIP Code");
    private String code;
    private String description;
    private DPVFootnoteType(String code, String description){
        this.code = code;
        this.description = description;
    }

    public static DPVFootnoteType getFootnoteType(String code){
        return Arrays.stream(DPVFootnoteType.values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unexpected DPV Footnote code : "+code));
    }
}
