package com.chatter.Chatly.websocket.message;

import com.chatter.Chatly.domain.chatroom.ChatRoom;
import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    public MessageService(MessageRepository messageRepository, MemberRepository memberRepository, ChatRoomRepository chatRoomRepository){
        this.messageRepository = messageRepository;
        this.memberRepository = memberRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Message saveMessage(Message message, String memberId, Long chatRoomId){
        // 최적화?
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new ResourceNotFoundException("Member not found with ID: " + memberId));
        // 채널에 속한 사용자인지는 최초 연결 시 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom not found with ID: " + chatRoomId));

        message.setMember(member);
        message.setChatRoom(chatRoom);
        Message saved = messageRepository.save(message);
        if(saved==null) throw new RuntimeException("Message creation failed");
        return saved;
    }
}
