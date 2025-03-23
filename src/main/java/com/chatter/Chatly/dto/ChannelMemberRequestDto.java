package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.ChannelMember;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ChannelMemberRequestDto {
    private Long channelId;
    private List<String> memberId;
}
