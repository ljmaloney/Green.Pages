package com.green.yp.usps.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addressType")
public class Address {
    @XmlAttribute(name="ID")
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    private Integer addressId;
    @XmlElement(name="Address1")
    @JsonProperty("Address1")
    private String addressLine1;
    @XmlElement(name="Address2")
    @JsonProperty("Address2")
    private String addressLine2;
    @XmlElement(name="City")
    @JsonProperty("City")
    private String city;
    @XmlElement(name = "State")
    @JsonProperty("State")
    private String state;
    @XmlElement(name="Zip5")
    @JsonProperty("Zip5")
    private String zip;
    @XmlElement(name="Zip4")
    @JsonProperty("Zip4")
    private String zip4;
}
