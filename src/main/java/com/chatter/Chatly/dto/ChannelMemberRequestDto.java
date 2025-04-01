package com.chatter.Chatly.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
