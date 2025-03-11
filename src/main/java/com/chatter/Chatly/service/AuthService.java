package com.chatter.Chatly.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatter.Chatly.config.JwtUtil;
import com.chatter.Chatly.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000 * 60 * 60L;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String userName, String password) {
        return JwtUtil.createJwt(userName, secretKey, expiredMs);
    }


    
}
