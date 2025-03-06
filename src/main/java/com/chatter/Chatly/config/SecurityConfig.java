package com.chatter.Chatly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 모두 허용 
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화 (POST 요청을 막지 않도록)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ 모든 요청 허용
                )
                .formLogin(form -> form.disable()) // ✅ 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()); // ✅ 기본 인증 비활성화 (선택)

        return httpSecurity.build();
    }
}

// 특정 url 허용
// @EnableWebSecurity
// @Configuration
// public class SecurityConfig {
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//         httpSecurity
//                 .csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/public/**").permitAll() // ✅ `/public/**` 경로 허용
//                         .anyRequest().authenticated() // ❌ 나머지는 인증 필요
//                 )
//                 .formLogin(withDefaults()) // ✅ 기본 로그인 폼 활성화
//                 .httpBasic(withDefaults()); // ✅ 기본 인증 활성화

//         return httpSecurity.build();
//     }
// }
