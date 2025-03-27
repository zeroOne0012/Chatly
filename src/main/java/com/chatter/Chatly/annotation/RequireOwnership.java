package com.chatter.Chatly.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 요청 Member가 특정 Entity의 소유자인지 검사
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireOwnership {
    Class<?> entityClass();       // 어떤 Entity인지
    String idParam();             // 어떤 파라미터가 ID인지
}
