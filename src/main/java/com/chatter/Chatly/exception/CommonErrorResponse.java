package com.chatter.Chatly.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드 제외
public class CommonErrorResponse {
    private final String error;
    private final String resource;

    public static CommonErrorResponse of(HttpException e) {
        CommonErrorCode errorCode = e.getErrorCode();
        String resource = getResourceInfo(e);
        // errorCode.getHttpStatus().getReasonPhrase(), 
        return new CommonErrorResponse(errorCode.getMessage(), resource);
    }    
    
    public static String getResourceInfo(HttpException e){
        if(e.getResourceClass()!=null && e.getResourceId()!=null){
            return e.getResourceClass().getSimpleName() + ": " + e.getResourceId();
        }
        return null;
    }
}
