package com.chatter.Chatly.config.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

// CustomHttpRequestWrapper가 모든 요청에서 적용될 수 있도록 Filter 설정
// Filter를 통해 Interceptor와 Controller 둘 다에서 Request Body를 읽을 수 있도록 할 수 있음
@Component
public class RequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            CustomHttpRequestWrapper requestWrapper = new CustomHttpRequestWrapper(httpRequest);
            chain.doFilter(requestWrapper, response);  // CustomHttpRequestWrapper로 감싼 후 체인 진행
        } else {
            chain.doFilter(request, response);
        }
    }
}