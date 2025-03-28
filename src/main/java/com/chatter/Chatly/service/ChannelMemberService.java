package com.chatter.Chatly.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.dto.ChannelMemberDto;
import com.chatter.Chatly.entity.Channel;
import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.entity.Role;
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
    private final AuthService authService;
    public ChannelMemberService(
        ChannelMemberRepository channelMemberRepository,
        MemberRepository memberRepository,
        ChannelRepository channelRepository,
        AuthService authService
        ){
        this.channelMemberRepository = channelMemberRepository;
        this.memberRepository = memberRepository;
        this.channelRepository = channelRepository;
        this.authService = authService;
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
        ChannelMember channelMember = channelMemberRepository.findByChannelAndMember(channel, member).orElse(null);
        return channelMember;
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
        String requestMemberId = authService.getMemberIdFromRequest();
        if(isJoined(channel_id, requestMemberId)==null){ // 채널에 속하지 않은 사용자가 초대 요청한다면
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"); // 요청 거부
        }
        List<ChannelMember> created = createChannelMembersReturnsEntity(channel_id, members_id);
        return created.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    // public ChannelMemberDto updateChannelMember(Long id, T dto) {
    //     ChannelMember channelMember = channelMemberRepository.findById(id)
    //     .orElseThrow(() -> new ResourceNotFoundException("ChannelMember not found with ID: " + id));
    // }

    // 권한 변경
    public List<ChannelMemberDto> updateChannelMembers(Long cid, List<String> memberId, Role role) {
        return memberId.stream()
        .map(mid->{
            ChannelMember channelMember = isJoined(cid, mid); 
            if (channelMember==null) throw new ResourceNotFoundException("ChannelMember not found");

            throwExceptionIfNoEditPrivilege(channelMember, cid, mid); // 권한 점검

            channelMember.setRole(role); // 권한 변경

            return ChannelMemberDto.from(channelMember);
        })
        .collect(Collectors.toList());
    }
    
    public void deleteChannelMember(Long cid, String mid) {
        ChannelMember channelMember = isJoined(cid, mid); 
        if (channelMember==null) throw new ResourceNotFoundException("ChannelMember not found");
        throwExceptionIfNoEditPrivilege(channelMember, cid, mid);
        channelMemberRepository.delete(channelMember);
    }

    private void throwExceptionIfNoEditPrivilege(ChannelMember channelMember, Long cid, String mid){
        String requestMemberId = authService.getMemberIdFromRequest();
        ChannelMember requestMembersConnection = isJoined(channelMember.getChannel().getId(), requestMemberId);
        if (!channelMember.getMember().getId().equals(requestMemberId) && // 본인이 아니면서
            !(requestMembersConnection!=null && requestMembersConnection.hasPrivilege())){ // 같은 채널의 관리자도 아니면
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"); // 채널 연결 삭제 요청 거부
        }
    }

}
