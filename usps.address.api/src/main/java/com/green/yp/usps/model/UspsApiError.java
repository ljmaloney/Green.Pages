package com.green.yp.usps.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name="Error")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Error")
@Data
public class UspsApiError {
    @XmlElement(name="Number")
    @JsonProperty("Number")
    private String errorNumber;
    @XmlElement(name="Source")
    @JsonProperty("Source")
    private String source;
    @XmlElement(name="Description")
    @JsonProperty("Description")
    private String description;
    @XmlElement(name="HelpFile")
    @JsonProperty("HelpFile")
    private String helpFile;
    @XmlElement(name="HelpContext")
    @JsonProperty("HelpContext")
    private String helpContext;
}