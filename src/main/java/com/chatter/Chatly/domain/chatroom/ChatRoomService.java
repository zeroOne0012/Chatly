package com.chatter.Chatly.domain.chatroom;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.chatroom.dto.ChatRoomDto;
import com.chatter.Chatly.domain.chatroom.dto.ChatRoomRequestDto;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChannelRepository channelRepository;
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChannelRepository channelRepository){
        this.chatRoomRepository = chatRoomRepository;
        this.channelRepository = channelRepository;
    }
    public List<ChatRoomDto> getAllChatRoom(Long cid) {
        Channel channel = channelRepository.findById(cid)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));

        return channel.getChatRooms().stream()
        .map(ChatRoomDto::from)
        .toList();
    }
    public ChatRoomDto createChatRoom(Long cid, ChatRoomRequestDto dto) {
        ChatRoom chatRoom = dto.toEntity();
        
        Channel channel = channelRepository.findById(cid)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));

        chatRoom.setChannel(channel);
        ChatRoom saved = chatRoomRepository.save(chatRoom);
        if(saved==null)throw new HttpException(CommonErrorCode.SAVE_FAILED, ChatRoom.class, "");
        return ChatRoomDto.from(chatRoom);
    }
    public ChatRoomDto updateChatRoomid(Long cid, Long id, ChatRoomRequestDto dto) {
        ChatRoom chatRoom = dto.toEntity();
        ChatRoom target = chatRoomRepository.findById(id)
        .orElseThrow(()->new HttpException(CommonErrorCode.NOT_FOUND, ChatRoom.class, id));
        if(!Objects.equals(cid, target.getChannel().getId())){
            throw new HttpException(CommonErrorCode.CHANNEL_CHATROOM_NOT_FOUND);
        }
        target.update(chatRoom);
        return ChatRoomDto.from(target);
    }
    public void deleteChatRoomid(Long cid, Long id) {
        ChatRoom target = chatRoomRepository.findById(id)
        .orElseThrow(()->new HttpException(CommonErrorCode.NOT_FOUND, ChatRoom.class, id));
        if(!Objects.equals(cid, target.getChannel().getId())){
            throw new HttpException(CommonErrorCode.CHANNEL_CHATROOM_NOT_FOUND);
        }
        chatRoomRepository.delete(target);
    }
}
