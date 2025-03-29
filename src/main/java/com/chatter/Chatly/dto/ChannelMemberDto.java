package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMemberDto {
    private Long channelId;
    private String memberId;
    private Role role;

    public static ChannelMemberDto from(ChannelMember channelMember){
        return new ChannelMemberDto(channelMember.getChannel().getId(), channelMember.getMember().getId(), channelMember.getRole());
    }
}
