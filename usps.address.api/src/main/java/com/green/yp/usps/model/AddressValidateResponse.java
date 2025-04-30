package com.green.yp.usps.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement(name="AddressValidateResponse")
@JacksonXmlRootElement(localName = "AddressValidateResponse")
public class AddressValidateResponse {
    @XmlElement(name="Address")
    @JacksonXmlProperty(localName = "Address")
    private AddressResponse addressResponse;
}
