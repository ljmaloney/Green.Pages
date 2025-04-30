package com.green.yp.usps.exception;

import lombok.Getter;

@Getter
public class UspsApiErrorException extends RuntimeException{
    private String errorNumber;
    private String description;
    private String source;

    public UspsApiErrorException(String errorNumber, String description, String source){
        super(String.format("%s - %s", errorNumber, description));
        this.errorNumber = errorNumber;
        this.description = description;
        this.source = source;
    }
}
