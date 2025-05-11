package com.chatter.Chatly.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorCode {
    
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid request parameter"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "Duplicate resource"),

    CANNOT_DELETE_ADMIN(HttpStatus.BAD_REQUEST, "The only ADMIN cannot be deleted"),
    
    CHANNEL_ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Article does not belong to the channel"),
    CHANNEL_CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ChatRoom does not belong to the channel"),
    ARTICLE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment does not belong to the article"),
    CHATROOM_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message does not belong to the chatroom"),
    REQUIRED_FIELD_EMPTY(HttpStatus.BAD_REQUEST, "Required field is empty."),
    
    SAVE_FAILED(HttpStatus.NOT_FOUND, "Failed to save data"),

    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "IO Exception"),
    
    INVALID_VALUE(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus httpStatus;
    private final String message;

    // CommonErrorCode(HttpStatus status, String message) {
    //     this.status = status;
    //     this.message = message;
    // }

    // public HttpStatus getHttpStatus() {
    //     return status;
    // }

    // public String getMessage() {
    //     return message;
    // }
}
