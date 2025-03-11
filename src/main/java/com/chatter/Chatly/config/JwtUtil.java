package com.chatter.Chatly.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    public static String createJwt(String userName, String secretKey, Long expiredMs){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//        Claims claims = Jwts.claims();
//        claims.put("userName", userName);
        return Jwts.builder()
                .claim("userName", userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}