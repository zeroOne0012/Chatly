package com.chatter.Chatly.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "log", name = "aop", havingValue = "true", matchIfMissing = false)
public class AspectLog {

	// @Value("${log.aop:false}") // 기본값 false
    // private boolean aopLogEnabled;

    @Around("execution(* com.chatter.Chatly..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("   [aop!] {}", joinPoint.getSignature()); // join point 시그니처
		return joinPoint.proceed();
	}
}