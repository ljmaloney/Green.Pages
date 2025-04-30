package com.green.yp.api.apitype.common;

import java.util.Arrays;
import lombok.Getter;
import org.apache.hc.core5.http.HttpStatus;

@Getter
public enum ErrorCodeType {

    BUSINESS_VALIDATION_ERROR("Business rule validation failed",
            HttpStatus.SC_PRECONDITION_FAILED),
    LINE_OF_BUSINESS_EXISTS("The line of business %s already exists",
            HttpStatus.SC_PRECONDITION_FAILED),
    NOT_FOUND("The requested resource was not found",
            HttpStatus.SC_NOT_FOUND),
    SYSTEM_ERROR("Unexpected internal error occurred",
            HttpStatus.SC_INTERNAL_SERVER_ERROR),
    UNEXPECTED_PAYMENT_ERROR("Unexpected payment error",
            HttpStatus.SC_INTERNAL_SERVER_ERROR),
    MAX_LOGIN_ATTEMPTS("Max login attempts exceeded, contact account is locked for {} minutes",
            HttpStatus.SC_LOCKED),
    RPC_CALL_FAILED("A remote system call failed, and we have been notified. Please try again later",
            HttpStatus.SC_INTERNAL_SERVER_ERROR);
    private final Integer errorCode;
    private final String messageFormat;

    ErrorCodeType(String messageFmt, int errorCode) {
        this.errorCode = errorCode;
        this.messageFormat = messageFmt;
    }

    public static ErrorCodeType findErrorCode(Integer code) {
        return Arrays.stream(ErrorCodeType.values())
                .filter(type -> type.errorCode.equals(code))
                .findFirst()
                .orElseGet(() -> ErrorCodeType.RPC_CALL_FAILED);
    }

    public static ErrorCodeType findErrorCode(String code) {
        return Arrays.stream(ErrorCodeType.values())
                .filter(type -> type.name().equals(code))
                .findFirst()
                .orElseGet(() -> ErrorCodeType.RPC_CALL_FAILED);
    }

}
