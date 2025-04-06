package com.chatter.Chatly.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.annotation.CheckAccessPossession;
import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.common.Ownable;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.dto.TargetsDto;
import com.chatter.Chatly.util.MemberContext;

import jakarta.persistence.EntityManager;

@Aspect
@Component
public class RequireCheckAspect {
    private final EntityManager entityManager;
    private final ChannelMemberService channelMemberService;
    private final MemberContext memberContext;

    public RequireCheckAspect(EntityManager entityManager, ChannelMemberService channelMemberService, MemberContext memberContext){
        this.entityManager = entityManager;
        this.channelMemberService = channelMemberService;
        this.memberContext = memberContext;
    }

    @Pointcut("@annotation(com.chatter.Chatly.annotation.RequirePrivilege)")
    private void requirePrivilege(){}
    @Pointcut("@annotation(com.chatter.Chatly.annotation.RequireOwnership))")
    private void requireOwnership(){}
    
    // @Before("requireOwnership()")
    // public void testAtop(String string){
    //     System.out.println("AOP WORKS!!");
    // }
    
    @Before("(requirePrivilege() || requireOwnership())")
    public void requireCheck(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePrivilege requirePrivilege = method.getAnnotation(RequirePrivilege.class);
        RequireOwnership requireOwnership = method.getAnnotation(RequireOwnership.class);

        boolean hasPrivilege = false;
        boolean hasOwnership = false;

        // 채널 관련 권한 검사
        if(requirePrivilege!=null){ // 관리 권한 확인 어노테이션
            Long cid = (Long)joinPoint.getArgs()[0]; // url의 첫 인자: cid

            // ChannelMember cm = channelMemberService.getChannelMemberFromRequest(cid);
            String mid = memberContext.getMemberIdFromRequest();
            ChannelMember cm = channelMemberService.isJoined(cid, mid);

            if(cm==null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            Role role = cm.getRole();
            if(role==Role.ADMIN || role==Role.MODERATOR){
                hasPrivilege=true;
            }
        }

        // 채널 내의 Entity 뿐 아니라 DM 같은 채널 외의 Entity도 검사 가능 (Member 검사는 MemberService에서)
        if(!hasPrivilege && requireOwnership!=null) { // 본인 확인 어노테이션
            Class<?> entityClass = requireOwnership.entityClass();
            int argIdx = requireOwnership.argIdx();
            
            // 파라미터 requireOwnership.argIdx()로 찾아 매핑
            Object[] args = joinPoint.getArgs();
            if(argIdx<0 || argIdx>=args.length){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid args index!");
            }
            
            Object targetArg = args[argIdx];
            List<Long> entityId = new ArrayList<>();
            if(targetArg instanceof Long aLong){
                entityId.add(aLong);
            } else if(targetArg instanceof TargetsDto){
                List<?> argList = (List<?>) ((TargetsDto)targetArg).getLst();
                if (!argList.isEmpty() && argList.getFirst() instanceof Long) {
                    entityId.addAll(argList.stream().map(eid->(Long)eid).toList());
                }
            } else{
                throw new RuntimeException("Unexpected param type: must be Long or LstDto");
            }
            
            if (entityId.isEmpty()) {
                throw new IllegalArgumentException("cannot find Entity ID");
            }
            
            hasOwnership = !entityId.stream()
                .anyMatch(eid -> { // anyMatch: 조건 맞으면 중단(return boolean), stream() 안에서 break 불가
                    // Entity 조회
                    Object entity = entityManager.find(entityClass, eid); // target-entity to edit

                    if (entity==null) {
                        throw new IllegalArgumentException("cannot find entity");
                    }
                    if (!(entity instanceof Ownable)) {
                        throw new IllegalArgumentException("Entity does not implement Ownable");
                    }

                    String targetEntitysMemberId = (String) ((Ownable<?>) entity).getOwnerId();
                    String requestersMemberId = memberContext.getMemberIdFromRequest();
                    
                    // 본인 것 외의 entity 감지
                    // anyMatch: true를 만나야 종료 -> true로 반환 후 반전하여 사용
                    return !targetEntitysMemberId.equals(requestersMemberId);
                });
            
        }
        if(!(hasPrivilege||hasOwnership)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    // 읽기 권한 - 채널에 속함 검사
    @Before("@annotation(com.chatter.Chatly.annotation.CheckAccessPossession)")
    public void checkPermission(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckAccessPossession checkAccessPossession = method.getAnnotation(CheckAccessPossession.class);

        int argIdx = checkAccessPossession.argIdx();
        Long cid = (Long)joinPoint.getArgs()[argIdx]; // url의 첫 인자: cid
        String memberId = memberContext.getMemberIdFromRequest();
        if(channelMemberService.isJoined(cid, memberId)==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
