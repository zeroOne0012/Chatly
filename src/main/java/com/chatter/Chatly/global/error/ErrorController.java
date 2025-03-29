package com.chatter.Chatly.global.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ErrorController {
    @RequestMapping(value = "/**")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound() {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", "해당 URL을 찾을 수 없습니다.");
        return errorResponse;
    }
}
