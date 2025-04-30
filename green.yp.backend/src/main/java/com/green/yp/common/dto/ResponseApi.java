package com.green.yp.common.dto;

public record ResponseApi<T>(T response, ErrorMessageApi errorMessageApi) {}
