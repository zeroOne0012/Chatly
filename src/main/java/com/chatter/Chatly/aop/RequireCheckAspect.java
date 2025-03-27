package com.chatter.Chatly.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.entity.Role;
import com.chatter.Chatly.service.AuthService;

@Aspect
@Component
public class RequireCheckAspect {
    private final AuthService authService;

    public RequireCheckAspect(AuthService authService){
        this.authService = authService;
    }

    @Before("(@annotation(requirePrivilege)||@annotation(requireOwnership)) && args(cid,..)")
    public void requireCheck(JoinPoint joinPoint, RequirePrivilege requirePrivilege, RequireOwnership requireOwnership, Long cid){
        boolean hasPrivilege = false;
        boolean hasOwnership = false;

        ChannelMember cm = authService.getChannelMemberFromRequest(cid);

        if(requirePrivilege!=null){ // 관리 권한 확인 어노테이션
            Role role = cm.getRole();
            if(role==Role.ADMIN || role==Role.MODERATOR){
                hasPrivilege=true;
            }
        }

        if(requireOwnership!=null){ // 본인 확인 어노테이션
            // some logic
        }

        if(!(hasPrivilege||hasOwnership)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근이 거부되었습니다.");
        }
        //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     String memberId = (String) auth.getPrincipal(); // JWT에서 추출된 memberId

    //     boolean hasPrivilege = channelMemberService.hasPrivilege(memberId, channelId, requirePrivilege.value());
    //     if (!hasPrivilege) {
    //         throw new AccessDeniedException("권한이 없습니다: " + requirePrivilege.value());
    //     }
    // }
    }

    @Before("(@annotation(checkPermissionToRead) && args(cid,..))")
    public void checkPermission(JoinPoint joinPoint){
        
    }
}
