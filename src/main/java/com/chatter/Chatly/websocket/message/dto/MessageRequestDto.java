package com.chatter.Chatly.websocket.message.dto;

import com.chatter.Chatly.websocket.message.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MessageRequestDto {
    private String message;
    @JsonProperty("chatroom")
    private Long chatRoomId;
    @JsonProperty("files")
    private List<String> fileUrl;
}
