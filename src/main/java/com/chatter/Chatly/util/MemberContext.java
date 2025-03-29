package com.chatter.Chatly.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberContext {
    @Value("${jwt.secret}")
    private String secretKey;

    public String getMemberIdFromRequest(){ // member ID 반환
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Claims claim = JwtUtil.extractClaims(token, secretKey);
        return (String) claim.get("member", Map.class).get("id");
    } 
}
