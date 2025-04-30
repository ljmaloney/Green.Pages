package com.green.yp.usps.enumeration;

import java.util.Arrays;

public enum FootnoteType {

    ZIP_CORRECTED("A",
            "Zip Code Corrected",
        """
            The address was found to have a different 5-digit Zip Code than given in the submitted list. 
            The correct Zip Code is shown in the output address.
            """),
    CITY_STATE_SPELLING("B",
            "City / State Spelling Corrected",
            """
                    The spelling of the city name and/or state abbreviation in the submitted address was found to 
                    be different than the standard spelling. The standard spelling of the city name and state 
                    abbreviation are shown in the output address.
                    """),
    INVALID_CITY_STATE_ZIP("C",
            "Invalid City / State / Zip",
            """
                    The Zip Code in the submitted address could not be found because neither a valid city, 
                    state, nor valid 5-digit Zip Code was present. It is also recommended that the requester
                     check the submitted address for accuracy.
                    """),
    NO_ZIP4_ASSIGNED("D",
            "No Zip+4 Assigned",
            """
                    This is a record listed by the United State Postal Service on the national Zip+4 file as a 
                    non-deliverable location. It is recommended that the requester verify the accuracy of the submitted address.
                    """),
    ZIP_CODE_MULTIPLE_RESPONSE("E",
            "Zip Code Assigned for Multiple Response",
            "Multiple records were returned, but each shares the same 5-digit Zip Code."),
    ADDRESS_NOT_FOUND("F","Address Could Not Be Found in The National Directory File Database",
            """
                    The address, exactly as submitted, could not be found in the city, state, or Zip Code provided."""),
    INFORMATION_IN_FIRM_LINE("G",
        "Information In Firm Line Used for Matching",
        """
            Information in the firm line was determined to be a part of the address. It was moved out of the 
            firm line and incorporated into the address line.
            """),
    MISSING_SECONDARY("H",
    "Missing Secondary Number",
            "Zip+4 information indicated this address is a building. The address as submitted does not contain an apartment/suite number."),
    INSUFFICIENT_INCORRECT_DATA("I",
            "Insufficient / Incorrect Address Data",
            """
            More than one Zip+4 was found to satisfy the address as submitted. The submitted address did not contain 
            sufficiently complete or correct data to determine a single Zip+4 Code.
            """),
    DUAL_ADDRESS("J","Dual Address","The input contained two addresses."),
    MULTIPLE_RESPONSE_CARDINAL_RULE("K",
            "Multiple Response Due to Cardinal Rule",
            "CASS rule does not allow a match when the cardinal point of a directional changes more than 90%."),
    ADDRESS_COMPONENT_CHANGED("L",
    "Address Component Changed","An address component was added, changed, or deleted in order to achieve a match."),
    STREET_NAME_CHANGED("M","Street Name Changed","The spelling of the street name was changed in order to achieve a match."),
    ADDRESS_STANDARDIZED("N","Address Standardized","The delivery address was standardized."),
    LOWEST_TIE_BREAKER("O","Lowest +4 Tie-Breaker",
            """
                More than one Zip+4 Code was found to satisfy the address as submitted. The 
                lowest Zip+4 addon may be used to break the tie between the records.
                """),
    BETTER_ADDRESS_EXISTS("P",
            "Better Address Exists",
            "The delivery address is matchable, but is known by another (preferred) name."),
    UNIQUE_ZIP_CODE("Q","Unique Zip Code Match","Match to an address with a unique Zip Code."),
    NO_MATCH_EWS("R",
            "No Match Due To EWS",
            "The delivery address is matchable, but the EWS file indicates that an exact match will be available soon."),
    INCORRECT_SECONDARDY("S",
    "Incorrect Secondary Address",
            """
                    The secondary information does not match that on the national Zip+4 file. This secondary information, 
                    although present on the input address, was not valid in the range found on the national Zip+4 file.
                    """),
    MULTIPLE_RESPONSE_MAGNET_STREET("T",
    "Multiple Response Due to Magnet Street Syndrome",
    "The search resulted on a single response; however, the record matched was flagged as having magnet street syndrome."),
    UNOFFICAL_POST_OFFICE_NAME("U",
    "Unofficial Post Office Name",
    """
        The city or post office name in the submitted address is not recognized by the United States Postal Service 
        as an official last line name (preferred city name) and is not acceptable as an alternate name."""),
    UNVERIFIABLE_CITY_STATE("V","Unverifiable City / State",
            "The city and state in the submitted address could not be verified as corresponding to the given 5-digit Zip Code."),
    INVALID_DELIVERY_ADDRESS("W",
            "Invalid Delivery Address",
            """
                    The input address record contains a delivery address other than a PO BOX, General Delivery, or 
                    Postmaster with a 5-digit Zip Code that is identified as a “small town default.” The United States Postal 
                    Service does not provide street delivery for this Zip Code. The United States Postal Service requires 
                    use of a PO BOX, General Delivery, or Postmaster for delivery within this Zip Code.
                    """),
    UNIQUE_ZIP_CODE_GENERATED("X","Unique Zip Code Generated", "Default match inside a unique Zip Code."),
    MILITARY_MATCH("Y","Military Match","Match made to a record with a military Zip Code."),
    ZIP_MOVE("Z","Match Mode Using the ZIPMOVE Product Data",
            "The ZIPMOVE product shows which Zip+4 records have moved from one Zip Code to another.");

    private String code;
    private String shortDescription;
    private String message;
    private FootnoteType(String code, String shortDescription, String message){
        this.code = code;
        this.shortDescription = shortDescription;
        this.message = message;
    }

    public static FootnoteType findType(String code){
        return Arrays.stream(FootnoteType.values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Invalid footnote code (%s) recieved from USPS", code)));
    }
}
