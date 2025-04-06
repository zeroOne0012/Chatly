package com.chatter.Chatly.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 각종 HTTP 에러
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ((HttpStatus) e.getStatusCode()).getReasonPhrase()); 
        errorResponse.put("message", e.getReason()); 

        return ResponseEntity
                .status(e.getStatusCode())
                .body(errorResponse);
    }

    // 삽입 충돌 에러(unique, primary key 등의 중복 값)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException (DataIntegrityViolationException e) {
        // Throwable rootCause = e.getRootCause(); // exception cause
        // if (rootCause != null && rootCause.getMessage() != null &&
        // rootCause.getMessage().toLowerCase().contains("duplicate")) { }     
        String lowerMessage = Optional.ofNullable(e.getRootCause())
            .map(Throwable::getMessage)
            .map(String::toLowerCase)
            .orElse("");
        Map<String, String> errorResponse = new HashMap<>();
        if (lowerMessage.contains("not-null") || lowerMessage.contains("null or transient")) {
            // 요청 body 필수값 누락
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Missing required field(s)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            // 기타 데이터 무결성 위반
            errorResponse.put("error", "Data Integrity Error");
            errorResponse.put("message", "Invalid data submitted");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 요청 param 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SaveFailedException.class)
    public ResponseEntity<Map<String, String>> handleSaveFailedException(SaveFailedException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Creation Failed");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", e.getMessage());
        log.error("Exception occurred!!!! : ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // 잘못된 파라미터/인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    // 잘못된 요청
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String,String>> handleInvalidRequestException(InvalidRequestException e){
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Entity 못찾음
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String,String>> handleNoSuchElementException(NoSuchElementException e){
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
