package com.chatter.Chatly.domain.chatroom;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.dto.ChatRoomDto;
import com.chatter.Chatly.dto.ChatRoomRequestDto;
import com.chatter.Chatly.exception.InvalidRequestException;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;

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
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));

        return channel.getChatRooms().stream()
        .map(ChatRoomDto::from)
        .toList();
    }
    public ChatRoomDto createChatRoom(Long cid, ChatRoomRequestDto dto) {
        ChatRoom chatRoom = dto.toEntity();
        
        Channel channel = channelRepository.findById(cid)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));

        chatRoom.setChannel(channel);
        ChatRoom saved = chatRoomRepository.save(chatRoom);
        if(saved==null)throw new SaveFailedException("Failed to save chatRoom");
        return ChatRoomDto.from(chatRoom);
    }
    public ChatRoomDto updateChatRoomid(Long cid, Long id, ChatRoomRequestDto dto) {
        ChatRoom chatRoom = dto.toEntity();
        ChatRoom target = chatRoomRepository.findById(id)
        .orElseThrow(()->new ResourceNotFoundException("ChatRoom not found with ID: " + id));
        if(!Objects.equals(cid, target.getChannel().getId())){
            throw new InvalidRequestException("ChatRoom does not belong to the channel");
        }
        target.update(chatRoom);
        return ChatRoomDto.from(target);
    }
    public void deleteChatRoomid(Long cid, Long id) {
        ChatRoom target = chatRoomRepository.findById(id)
        .orElseThrow(()->new ResourceNotFoundException("ChatRoom not found with ID: " + id));
        if(!Objects.equals(cid, target.getChannel().getId())){
            throw new InvalidRequestException("ChatRoom does not belong to the channel");
        }
        chatRoomRepository.delete(target);
    }
}
