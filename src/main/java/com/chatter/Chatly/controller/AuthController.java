package com.chatter.Chatly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.dto.UserDto;
import com.chatter.Chatly.dto.UserRequestDto;
import com.chatter.Chatly.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRequestDto requestDto) {
        UserDto user = authService.createUser(requestDto);
        return ResponseEntity.ok(user);
    }
}
