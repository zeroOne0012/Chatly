package com.chatter.Chatly.websocket.message;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.chatroom.ChatRoom;
import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.websocket.message.dto.MessageRequestDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberService channelMemberService;

    public Message saveMessage(MessageRequestDto dto, Principal principal){
        String memberId = principal.getName();

        Member member = memberRepository.findById(memberId)
                // .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND,Member.class,memberId));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId())
                // .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND,ChatRoom.class,dto.getChatRoomId()));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));

        // 채널(채팅방)에 속함 검사 // 첫 연결 시 검사로 변경할 수 있을 것
        Channel channel = channelRepository.findById(chatRoom.getChannel().getId())
                // .orElseThrow(() ->new HttpException(CommonErrorCode.NOT_FOUND,Channel.class,chatRoom.getChannel().getId()));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));
        ChannelMember cm =  channelMemberService.isJoined(channel.getId(), member.getId());
        if(cm==null){
            log.error("Member does not belongs to the ChatRoom(Channel)");
            throw new RuntimeException("Member does not belongs to the ChatRoom(Channel)");
        }

        Message message = Message.builder()
                .message(dto.getMessage())
                .member(member)
                .chatRoom(chatRoom)
//                .files(dto.getFileUrl()==null ? null : new ArrayList<>(dto.getFileUrl().stream().map(File::getFileUrl).toList()))
                .files(null)
                .build();

        Message saved = messageRepository.save(message);
        if(saved==null) throw new RuntimeException("Message creation failed");
        return saved;
    }
}
