package com.chatter.Chatly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.dto.UserDto;
import com.chatter.Chatly.dto.UserRequestDto;
import com.chatter.Chatly.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    // @GetMapping

    // @PutMapping
    // @DeleteMapping
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRequestDto requestDto) {
        UserDto user = userService.createUser(requestDto);
        return ResponseEntity.ok(user);
    }
}
