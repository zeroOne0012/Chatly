package com.chatter.Chatly.domain.like;

import com.chatter.Chatly.domain.like.dto.LikeDto;
import com.chatter.Chatly.domain.like.dto.LikeRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    public LikeDto createLike(Long cid, LikeRequestDto dto){
        return null;
    }
    public LikeDto deleteLike(Long cid, Long likeId){
        return null;
    }
}
