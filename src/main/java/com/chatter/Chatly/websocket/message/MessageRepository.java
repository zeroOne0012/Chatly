package com.chatter.Chatly.websocket.message;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.domain.chatroom.ChatRoom;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom(ChatRoom chatRoom);

    List<Message> findTopByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);
    List<Message> findByChatRoomIdAndIdLessThanOrderByIdDesc(Long chatRoomId, Long lastMessageId, Pageable pageable);

}
