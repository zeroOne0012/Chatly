package com.chatter.Chatly.global.auth;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.domain.member.dto.MemberRequestDto;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> login(@RequestBody MemberRequestDto requestDto) {
        String token = authService.login(requestDto.getId(), requestDto.getPassword());
        HashMap<String, String> res = new HashMap<>();
        res.put("message", "success");
        res.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    

}
