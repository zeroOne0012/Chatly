package com.chatter.Chatly.domain.channelmember.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMemberRequestDto {
    // @JsonProperty("channel_id")
    private Long channelId;
    // @JsonProperty("member_id")
    private List<String> memberId;
}
