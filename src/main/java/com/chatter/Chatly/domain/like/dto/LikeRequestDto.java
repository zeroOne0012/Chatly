package com.chatter.Chatly.domain.like.dto;

import com.chatter.Chatly.domain.like.Like;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeRequestDto {
    private String entityType;
    private Long entityId;
//    public Like toEntity(){
//        return new Like(entityType, entityId);
//    }
}
