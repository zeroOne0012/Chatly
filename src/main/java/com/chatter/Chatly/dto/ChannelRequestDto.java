package com.chatter.Chatly.dto;

import com.chatter.Chatly.domain.channel.Channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ChannelRequestDto {
    private String channelName;

    public Channel toEntity(){
        return new Channel(channelName);
    }
}
