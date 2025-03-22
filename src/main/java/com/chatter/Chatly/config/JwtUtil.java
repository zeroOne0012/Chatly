package com.chatter.Chatly.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import com.chatter.Chatly.entity.Memeber;

public class JwtUtil {
    public static String createJwt(Memeber member, String secretKey, Long expiredMs){
        // Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Key key = getSigningKey(secretKey);
//        Claims claims = Jwts.claims();
//        claims.put("memberName", memberName);
        return Jwts.builder()
                .claim("member", member)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    
    public static boolean isExpired(String token, String secretKey) {
        return extractClaims(token, secretKey).getExpiration().before(new Date());
    }

    private static Claims extractClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey)) // 최신 방식 적용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // private static Key getSigningKey(String secretKey) {
    //     byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64 디코딩
    //     return Keys.hmacShaKeyFor(keyBytes);
    // }
    private static Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}