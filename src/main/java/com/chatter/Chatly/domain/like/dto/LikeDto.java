package com.chatter.Chatly.domain.like.dto;

import com.chatter.Chatly.domain.like.Like;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeDto {
    private Long id;
    private String memberId;
    private Long channelId;
    private String entityType;
    private Long entityId;
    public LikeDto from(Like like){
        return new LikeDto(
                like.getId(),
                like.getChannelMember().getMember().getId(),
                like.getChannelMember().getChannel().getId(),
                like.getEntityType(),
                like.getEntityId()
        );
    }
}
