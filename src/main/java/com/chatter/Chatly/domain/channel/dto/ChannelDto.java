package com.chatter.Chatly.domain.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.domain.channel.Channel;

import com.chatter.Chatly.domain.channelmember.dto.ChannelMemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelDto {
    private Long id;
    private String channelName;
    private String profileUrl;
    private List<ChannelMemberDto> channelMembers;
    private LocalDateTime createdAt;

    public static ChannelDto from(Channel channel) {
        List<ChannelMemberDto> members = new ArrayList<>();
        
        if (channel.getChannelMembers() != null) {
            members = channel.getChannelMembers().stream()
            .map(ChannelMemberDto::from)
            .toList();
            // for (ChannelMember entity : channel.getChannelMembers()) {
            //     members.add(ChannelMemberDto.from(entity));
            // }
        }
        
        return new ChannelDto(
            channel.getId(), 
            channel.getChannelName(), 
            channel.getProfileUrl(), 
            members, 
            channel.getCreatedAt()
        );
    }
    
}
