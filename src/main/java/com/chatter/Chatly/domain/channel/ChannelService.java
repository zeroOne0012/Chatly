package com.chatter.Chatly.domain.channel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.chatroom.ChatRoom;
import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.domain.channel.dto.ChannelDto;
import com.chatter.Chatly.domain.channel.dto.ChannelRequestDto;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.util.MemberContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelMemberService channelMemberService;
    private final MemberContext memberContext;
    private final ChatRoomRepository chatRoomRepository;
    public ChannelService(
        ChannelRepository channelRepository,
        ChannelMemberService channelMemberService,
        MemberContext memberContext,
        ChatRoomRepository chatRoomRepository
        ){
        this.channelRepository = channelRepository;
        this.channelMemberService = channelMemberService;
        this.memberContext = memberContext;
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<ChannelDto> getAllChannel() {
        return channelRepository.findAll().stream()
        .map(ChannelDto::from)
        .toList();
    }

    public ChannelDto getChannelById(Long id){
        Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, id));
        return ChannelDto.from(channel);
    }

    public ChannelDto createChannel(ChannelRequestDto dto) {
        Channel created = channelRepository.save(dto.toEntity());
        if(created==null)throw new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, ""); // Dead Code?
        String mid = memberContext.getMemberIdFromRequest(); // 채널 개설자 채널멤버에 추가
        List<ChannelMember> cm = channelMemberService.createChannelMembersReturnsEntity(created.getId(), List.of(mid));
        if(cm.size()!=1){
            throw new HttpException(CommonErrorCode.INVALID_VALUE, ChannelMember.class, "cm.size() must be 1");
        }
        // chatRoom 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChannel(created);
        chatRoomRepository.save(chatRoom);
        
        cm.getFirst().setRole(Role.ADMIN);
        return ChannelDto.from(created);
    }

    public ChannelDto updateChannel(Long id, ChannelRequestDto dto) {
        Channel channel = dto.toEntity();
        Channel target = channelRepository.findById(id)
            .orElseThrow(()-> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, id));
        target.update(channel);
        return ChannelDto.from(target);
    }
    
    public void deleteChannel(Long id) {
        Channel channel = channelRepository.findById(id)
            .orElseThrow(()-> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, id));
        channelRepository.delete(channel);
    }
}
