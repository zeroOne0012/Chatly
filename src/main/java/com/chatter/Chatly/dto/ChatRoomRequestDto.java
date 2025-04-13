package com.chatter.Chatly.dto;

import com.chatter.Chatly.domain.chatroom.ChatRoom;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {
    private String name;

    public ChatRoom toEntity(){
        return new ChatRoom(this.name);
    }
}
