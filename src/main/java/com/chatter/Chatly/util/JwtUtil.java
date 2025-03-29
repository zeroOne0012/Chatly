package com.chatter.Chatly.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.chatter.Chatly.domain.member.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

public class JwtUtil {
    public static String createJwt(Member member, String secretKey, Long expiredMs){
        // Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Key key = getSigningKey(secretKey);
//        Claims claims = Jwts.claims();
//        claims.put("memberName", memberName);

        // 필요 정보만 추출
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("id", member.getId());
        memberData.put("email", member.getEmail());
        memberData.put("nickname", member.getNickname());
        memberData.put("createdAt", member.getCreatedAt().toString());


        return Jwts.builder()
                .claim("member", memberData)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    
    public static boolean isExpired(String token, String secretKey) {
        return extractClaims(token, secretKey).getExpiration().before(new Date());
    }

    // default: 같은 패키지 내에서 호출
    static Claims extractClaims(String token, String secretKey) {
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