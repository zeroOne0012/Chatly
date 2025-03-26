package com.chatter.Chatly.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.service.AuthService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
public class RequireCheckAspect {
    private final AuthService authService;

    public RequireCheckAspect(AuthService authService){
        this.authService = authService;
    }

    @Before("(@annotation(requirePrivilege)||@annotation(requireOwnership)) && args(cid,..)")
    public void requireCheck(JoinPoint joinPoint, RequirePrivilege requirePrivilege, RequireOwnership requireOwnership, Long channelId) {
        boolean hasPrivilege = false;
        boolean hasOwnership = false;

        String mid = authService.getMemberIdFromRequest();

        if(requirePrivilege!=null){ // 관리 권한 확인인 어노테이션션
            
        }
        //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     String memberId = (String) auth.getPrincipal(); // JWT에서 추출된 memberId

    //     boolean hasPrivilege = channelMemberService.hasPrivilege(memberId, channelId, requirePrivilege.value());
    //     if (!hasPrivilege) {
    //         throw new AccessDeniedException("권한이 없습니다: " + requirePrivilege.value());
    //     }
    // }
    }
}

// 채널마다 권한 설정: ChannelMemberService 구현 -> 위 클래스 등록 -> @RequirePrivilege("SOME_PRIVILEGE") 실제 사용

