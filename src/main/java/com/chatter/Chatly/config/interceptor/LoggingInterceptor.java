package com.chatter.Chatly.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        int clientPort = request.getRemotePort();

        log.info("\n\nClient: {}:{}", clientIp, clientPort);

        // // PathVariable Logging
        // if (handler instanceof HandlerMethod) {
        //     ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //     if (attributes != null) {
        //         HttpServletRequest currentRequest = attributes.getRequest();
        //         Map<String, String> pathVariables = (Map<String, String>) currentRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        //         if (pathVariables != null) {
        //             log.info("Request Method: [{}] URL: [{}] pathVariables: [{}]", request.getMethod(), request.getRequestURI(), pathVariables);
        //             return true;  // 요청을 계속 진행
        //         }
        //     }
        // }

        // Request Param 존재한다면 Logging 한다.
        if (request.getParameterNames().hasMoreElements()) {
            log.info("Request Method: [{}] URL: [{}] Params: [{}]",request.getMethod(), request.getRequestURI(), getRequestParams(request));
        }
        else {
            log.info("Request Method: [{}] URL: [{}]",request.getMethod(), request.getRequestURI());
        }

        // request가 CustomHttpRequestWrapper로 래핑되어 있는지 확인
        if (request instanceof CustomHttpRequestWrapper) {
            CustomHttpRequestWrapper requestWrapper = (CustomHttpRequestWrapper) request;
            String requestBody = new String(requestWrapper.getRequestBody());

            // Request Body가 있을 경우 로깅
            if (!requestBody.isEmpty()) {
                log.info("Body: [{}]", requestBody);
            }
        }

        return true;  // 요청을 계속 진행
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws JsonProcessingException {
        // Response Logging
        if (response instanceof CustomHttpResponseWrapper responseWrapper) {
            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0) {
                String responseBody = new String(responseData);

                ObjectMapper mapper = new ObjectMapper();
                Object json = mapper.readValue(responseBody, Object.class);
                String prettyBody = mapper.writeValueAsString(json);

                log.info("Response Status: [{}] URL: [{}] \nBody: [{}]", response.getStatus(), request.getRequestURI(), prettyBody);
            } else {
                log.info("Response Status: [{}] URL: [{}] Body: [Empty]", response.getStatus(), request.getRequestURI());
            }
        } else {
            log.info("Response Status: [{}] URL: [{}]", response.getStatus(), request.getRequestURI());
        }
    }


    // 쿼리 파라미터를 Map 형태로 추출
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap;
    }
}