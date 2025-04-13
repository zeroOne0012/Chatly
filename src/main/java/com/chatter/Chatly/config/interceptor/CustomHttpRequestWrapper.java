package com.chatter.Chatly.config.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// for request body logging
/*
 * Request Body를 로깅할 때 주의할 점
 * HttpServletRequest의 Request Body는 한 번 읽으면 다시 읽을 수 없습니다. 
 * 즉, Interceptor에서 Request Body를 읽어 로깅하면, 
 * 컨트롤러에서 해당 바디를 다시 읽을 수 없게 되어 정상적인 처리가 불가능해집니다.
 * https://velog.io/@dongjae0803/Spring-Boot-%EB%A1%9C%EA%B7%B8-%EA%B4%80%EB%A6%AC-%EC%9E%98%ED%95%98%EA%B3%A0-%EA%B3%84%EC%8B%A0%EA%B0%80%EC%9A%94-Logging
 */
public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // Request Body를 바이트 배열로 저장해 여러 번 읽을 수 있도록 캐싱
        InputStream is = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        this.requestBody = byteArrayOutputStream.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.requestBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented
            }
        };
    }

    public byte[] getRequestBody() {
        return this.requestBody;
    }
}