package com.chatter.Chatly.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스를 서빙 경로 매핑
        registry.addResourceHandler("/uploads/**")  // URL 접근 경로
                .addResourceLocations("file:uploads/"); // 실제 파일 경로(로컬)
    }
}

