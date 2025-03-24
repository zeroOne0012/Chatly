package com.chatter.Chatly.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatter.Chatly.config.JwtUtil;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.repository.MemberRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;
    // private final Long expiredMs = 1000 * 60 * 60L;

    private Long expiredMs;

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public AuthService(
            MemberRepository memberRepository, 
            BCryptPasswordEncoder passwordEncoder, 
            @Value("${jwt.token-validity-in-seconds}") String seconds
        ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.expiredMs = 1000L * Integer.parseInt(seconds);
    }

    public String login(String id, String password) {
        // Member member = memberRepository.findByIdAndPassword(id, bCryptPassword);
        Member member = memberRepository.findById(id).orElse(null);
        if(member==null){
            throw new NoSuchElementException("Check your ID/PSWD");
        }
        if(!passwordEncoder.matches(password, member.getPassword())){
            throw new NoSuchElementException("Check your ID/PSWD!!!");
        }

        return JwtUtil.createJwt(member, secretKey, expiredMs);
    }    
}
