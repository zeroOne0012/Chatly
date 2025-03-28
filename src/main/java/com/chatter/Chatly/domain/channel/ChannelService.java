package com.chatter.Chatly.domain.channel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.dto.ChannelDto;
import com.chatter.Chatly.dto.ChannelRequestDto;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
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
    public ChannelService(
        ChannelRepository channelRepository,
        ChannelMemberService channelMemberService,
        MemberContext memberContext
        ){
        this.channelRepository = channelRepository;
        this.channelMemberService = channelMemberService;
        this.memberContext = memberContext;
    }

    public List<ChannelDto> getAllChannel() {
        return channelRepository.findAll().stream()
        .map(ChannelDto::from)
        .toList();
    }

    public ChannelDto getChannelById(Long id){
        Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + id));
        return ChannelDto.from(channel);
    }

    public ChannelDto createChannel(ChannelRequestDto dto) {
        Channel created = channelRepository.save(dto.toEntity());
        if(created==null)throw new SaveFailedException("Failed to create Channel"); // Dead Code?
        String mid = memberContext.getMemberIdFromRequest();
        List<ChannelMember> cm = channelMemberService.createChannelMembersReturnsEntity(created.getId(), List.of(mid));
        if(cm.size()!=1){
            throw new RuntimeException("cm.size() must be 1");
        }
        cm.getFirst().setRole(Role.ADMIN);
        return ChannelDto.from(created);
    }

    public ChannelDto updateChannel(Long id, ChannelRequestDto dto) {
        Channel channel = dto.toEntity();
        Channel target = channelRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Channel not found with ID: " + id));
        target.update(channel);
        return ChannelDto.from(target);
    }
    
    public void deleteChannel(Long id) {
        Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found"));
        channelRepository.delete(channel);
    }
}
