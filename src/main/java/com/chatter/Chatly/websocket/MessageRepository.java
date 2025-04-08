package com.chatter.Chatly.websocket;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.domain.chatroom.ChatRoom;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom(ChatRoom chatRoom);
}
