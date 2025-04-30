package com.green.yp.usps.exception;

public class UspsApiException extends RuntimeException{
    public UspsApiException(String message, Throwable th){
        super(message, th);
    }
}
