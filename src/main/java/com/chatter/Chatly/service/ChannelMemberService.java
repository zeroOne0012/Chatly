package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.ChannelMemberDto;
import com.chatter.Chatly.entity.Channel;
import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
import com.chatter.Chatly.repository.ChannelMemberRepository;
import com.chatter.Chatly.repository.ChannelRepository;
import com.chatter.Chatly.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    public ChannelMemberService(
        ChannelMemberRepository channelMemberRepository,
        MemberRepository memberRepository,
        ChannelRepository channelRepository
        ){
        this.channelMemberRepository = channelMemberRepository;
        this.memberRepository = memberRepository;
        this.channelRepository = channelRepository;
    }

    public List<ChannelMemberDto> getAllChannelMembers() {
        return channelMemberRepository.findAll().stream()
        .map(ChannelMemberDto::from)
        .toList();
    }

    public ChannelMember isJoined(Long channelId, String memberId){
        Channel channel = channelRepository.findById(channelId).orElse(null);
        Member member = memberRepository.findById(memberId).orElse(null);
        if(channel==null || member==null) {
            throw new ResourceNotFoundException("ChannelMember not found with [channel,member]: [" + channelId + ", " + memberId + "]");
        };
        ChannelMember channelMember = channelMemberRepository.findByChannelAndMember(channel, member)
        .orElseThrow(() -> new ResourceNotFoundException("ChannelMember not found with [channel,member]: [" + channelId + ", " + memberId + "]"));
        return channelMember;
        // return ChannelMemberDto.from(channelMember);
    }

    public List<ChannelMemberDto> getChannelMembersByChannelId(Long id){
        Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));
        List<ChannelMember> channelMember = channelMemberRepository.findByChannel(channel)
        .orElseThrow(() -> new ResourceNotFoundException("ChannelMember not found with channel ID: " + id));
        return channelMember.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    public List<ChannelMemberDto> getChannelMembersByMemberId(String id){
        Member member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        List<ChannelMember> channelMember = channelMemberRepository.findByMember(member)
        .orElseThrow(() -> new ResourceNotFoundException("ChannelMember not found with member ID: " + id));
        return channelMember.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    public List<ChannelMember> createChannelMembersReturnsEntity(Long channel_id, List<String> members_id){
        Channel channel = channelRepository.findById(channel_id)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channel_id));
        List<Member> members = members_id.stream()
            .map(id->memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id)))
            .toList();


        List<ChannelMember> created = members.stream()
            .map(member -> channelMemberRepository.save(new ChannelMember(channel, member)))
            .toList();

        if(created==null || created.size()!=members_id.size()) throw new SaveFailedException("Failed to create ChannelMember"); // Dead Code?
        return created;
    }

    public List<ChannelMemberDto> createChannelMembers(Long channel_id, List<String> members_id){
        List<ChannelMember> created = createChannelMembersReturnsEntity(channel_id, members_id);
        return created.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    // public ChannelMemberDto updateChannelMember(Long id, T dto) {
    //     ChannelMember channelMember = channelMemberRepository.findById(id)
    //     .orElseThrow(() -> new ResourceNotFoundException("ChannelMember not found with ID: " + id));
    // }
    
    public void deleteChannelMember(Long cid, String mid) {
        ChannelMember channelMember = isJoined(cid, mid);
        if (channelMember==null) throw new ResourceNotFoundException("ChannelMember not found");
        channelMemberRepository.delete(channelMember);
    }
}
