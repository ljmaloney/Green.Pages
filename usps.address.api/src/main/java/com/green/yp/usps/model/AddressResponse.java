package com.green.yp.usps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NegativeOrZero;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {
    @XmlAttribute(name="ID")
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    private Integer addressId;
    @XmlElement(name="Address1")
    @JacksonXmlProperty(localName = "Address1", isAttribute = true)
    private String addressLine1;
    @XmlElement(name="Address2")
    @JacksonXmlProperty(localName = "Address2", isAttribute = true)
    private String addressLine2;
    @XmlElement(name="Business")
    @JacksonXmlProperty(localName = "Business", isAttribute = true)
    private String business;
    @XmlElement(name="CarrierRoute")
    @JacksonXmlProperty(localName = "CarrierRoute", isAttribute = true)
    private String carrierRoute;
    @XmlElement(name="CentralDeliveryPoint")
    @JacksonXmlProperty(localName = "CentralDeliveryPoint", isAttribute = true)
    private String centralDeliveryPoint;
    @XmlElement(name="City")
    @JacksonXmlProperty(localName = "City", isAttribute = true)
    private String city;
    @XmlElement(name="CityAbbreviation")
    @JacksonXmlProperty(localName = "CityAbbreviation", isAttribute = true)
    private String cityAbbreviation;
    @XmlElement(name="DeliveryPoint")
    @JacksonXmlProperty(localName = "DeliveryPoint", isAttribute = true)
    private String deliveryPoint;
    @XmlElement(name="DPVConfirmation")
    @JacksonXmlProperty(localName = "DPVConfirmation", isAttribute = true)
    private String dpvConfirmation;
    @XmlElement(name="DPVCMRA")
    @JacksonXmlProperty(localName = "DPVCMRA", isAttribute = true)
    private String dpvCmra;
    @XmlElement(name="DPVFootnotes")
    @JacksonXmlProperty(localName = "DPVFootnotes", isAttribute = true)
    private String dpvFootnotes;
    @XmlElement(name="Footnotes")
    @JacksonXmlProperty(localName = "Footnotes", isAttribute = true)
    private String footNotes;
    @XmlElement(name = "State")
    @JacksonXmlProperty(localName = "State", isAttribute = true)
    private String state;
    @XmlElement(name="Vacant")
    @JacksonXmlProperty(localName = "Vacant", isAttribute = true)
    private String vacant;
    @XmlElement(name="Zip5")
    @JacksonXmlProperty(localName = "Zip5", isAttribute = true)
    private String zip;
    @XmlElement(name="Zip4")
    @JacksonXmlProperty(localName = "Zip4", isAttribute = true)
    private String zip4;
}
