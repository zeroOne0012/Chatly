package com.chatter.Chatly.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid request parameter"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    
    CHANNEL_ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Article does not belong to the channel"),
    REQUIRED_FIELD_EMPTY(HttpStatus.BAD_REQUEST, "Required field is empty."),

    ARTICLE_SAVE_FAILED(HttpStatus.NOT_FOUND, "Failed to save article"),
    
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;

    CommonErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
