package com.green.yp.usps.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@XmlRootElement(name="AddressValidateRequest")
@JacksonXmlRootElement(localName = "AddressValidateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressValidateRequest {
    @XmlAttribute(name = "USERID")
    @JacksonXmlProperty(localName = "USERID", isAttribute = true)
    private String userId;
    @XmlElement(name = "Revision")
    @JacksonXmlProperty(localName = "Revision")
    private Integer revision;
    @XmlElement(name="Address")
    @JacksonXmlProperty(localName = "Address")
    private Address address;
}
