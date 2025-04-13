package com.chatter.Chatly.dto;

import com.chatter.Chatly.domain.chatroom.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String name;
    public static ChatRoomDto from(ChatRoom chatRoom){
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }
}
