package com.chatter.Chatly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Repository save 실패
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SaveFailedException extends RuntimeException {
    public SaveFailedException(String message) {
        super(message);
    }
}