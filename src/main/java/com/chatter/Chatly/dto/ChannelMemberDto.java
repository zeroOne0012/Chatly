package com.chatter.Chatly.dto;

import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.common.Role;

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
