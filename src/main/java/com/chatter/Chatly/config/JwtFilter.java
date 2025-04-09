package com.chatter.Chatly.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chatter.Chatly.util.JwtUtil;
import com.chatter.Chatly.util.MemberContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final MemberContext memberContext;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
    
        // 로그인과 회원가입 요청은 필터링 X
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/member/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // logger.info("authorization = " + authorization);
        
        if(authorization == null || !authorization.startsWith("Bearer ")){
            // logger.error("authorization 이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Token 꺼내기
        String token = authorization.split(" ")[1];
        
        // Token Expired 되었는지 여부
        try{
            JwtUtil.isExpired(token, secretKey);
        } catch(Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;
        }


        // MemberName Token에서 꺼내기
        String memberId = (String) memberContext.getMemberIdFromRequest();

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, null, List.of(new SimpleGrantedAuthority("Member")));

        // Detail을 넣어준다.
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}