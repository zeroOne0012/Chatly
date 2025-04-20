package com.chatter.Chatly.domain.like;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.comment.Comment;
import com.chatter.Chatly.domain.like.dto.LikeDto;
import com.chatter.Chatly.domain.like.dto.LikeRequestDto;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.websocket.message.Message;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final EntityManager entityManager;
    private final LikeRepository likeRepository;
//    private final MemberRepository memberRepository;
    private final ChannelMemberService channelMemberService;

    // class name mapping
    private static final Map<String, Class<?>> ENTITY_TYPE_MAP = Map.of(
            "ARTICLE", Article.class,
            "COMMENT", Comment.class,
            "MESSAGE", Message.class
    );

    // like 증가
    private void incrementLikes(Object entity, boolean increase) {
        try {
            // 클래스 객체 가져오기
            Class<?> className = entity.getClass();

            // "likes" 필드 가져오기
            Field likesField = className.getDeclaredField("likes");

            // private 필드 접근 가능 설정
            likesField.setAccessible(true);

            // likes 현재 값 가져오기
            Object currentValue = likesField.get(entity);
            if (currentValue instanceof Long currentLikes) {
                // likes 값 +-1
                likesField.set(entity, currentLikes + (increase?1:-1));
                if((Long)likesField.get(entity)<0){
                    throw new HttpException(CommonErrorCode.INVALID_VALUE); // 500
                }
            } else {
                throw new HttpException(CommonErrorCode.INVALID_PARAMETER);
            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException("likes 필드를 수정하는 중 오류 발생", e);
//        }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new HttpException(CommonErrorCode.INTERNAL_SERVER_ERROR, Like.class, e);
        }

    }

    // conflict 검사
    private boolean isConstraintViolation(Exception e) {
        // DataIntegrityViolationException ?
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public LikeDto createLike(Long cid, LikeRequestDto dto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId = (String) auth.getPrincipal();
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(()->new HttpException(CommonErrorCode.NOT_FOUND, Member.class, memberId));
        ChannelMember channelMember = channelMemberService.isJoined(cid, memberId);
        Like like = new Like(channelMember, dto.getEntityType(), dto.getEntityId());
        Like created;
        try{
            created = likeRepository.save(like);
        } catch (Exception e){ // conflict 검사
            if (isConstraintViolation(e)) {
                throw new HttpException(CommonErrorCode.CONFLICT, Like.class, channelMember.getMember().getId()+", "+dto.getEntityType()+", "+dto.getEntityId()); // 혹은 기존 데이터 조회 후 반환
            }
            throw new HttpException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }


        // get class name
        Class<?> className = ENTITY_TYPE_MAP.get(dto.getEntityType().toUpperCase());
        if (className == null) throw new HttpException(CommonErrorCode.INVALID_PARAMETER);

        // like count
        Object entity = entityManager.find(className, dto.getEntityId());
        if(entity==null) throw new HttpException(CommonErrorCode.NOT_FOUND, className, dto.getEntityId());
        incrementLikes(entity, true);

        return LikeDto.from(created);
    }
    public void deleteLike(Long cid, Long likeId){
        Like like = likeRepository.findById(likeId)
                .orElseThrow(()->new HttpException(CommonErrorCode.NOT_FOUND, Like.class, likeId));
        likeRepository.delete(like);

        // get class name
        Class<?> className = ENTITY_TYPE_MAP.get(like.getEntityType().toUpperCase());
        if (className == null) throw new HttpException(CommonErrorCode.INVALID_PARAMETER);

        // like count
        Object entity = entityManager.find(className, like.getEntityId());
        if(entity==null) throw new HttpException(CommonErrorCode.NOT_FOUND, className, like.getEntityId());
        incrementLikes(entity, false);
    }
}
