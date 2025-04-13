package com.chatter.Chatly.exception;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {
    private final CommonErrorCode errorCode;
    private final Class<?> resourceClass;
    private final Object resourceId; // id or info-msg
    
    public HttpException(CommonErrorCode errorCode) {
        this.errorCode = errorCode;
        this.resourceClass = null;
        this.resourceId = null;
    }
    public HttpException(CommonErrorCode errorCode, Class<?> resourceClass, Object resourceId) {
        this.errorCode = errorCode;
        this.resourceClass = resourceClass;
        this.resourceId = resourceId;
    }
}