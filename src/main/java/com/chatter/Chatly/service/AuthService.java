package com.chatter.Chatly.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatter.Chatly.config.JwtUtil;
import com.chatter.Chatly.entity.User;
import com.chatter.Chatly.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;
    private final Long expiredMs = 1000 * 60 * 60L;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String id, String password) {
        // User user = userRepository.findByIdAndPassword(id, bCryptPassword);
        User user = userRepository.findById(id).orElse(null);
        if(user==null){
            throw new NoSuchElementException("Check your ID/PSWD");
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new NoSuchElementException("Check your ID/PSWD!!!");
        }

        return JwtUtil.createJwt(user, secretKey, expiredMs);
    }


    
}
