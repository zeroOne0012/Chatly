package com.chatter.Chatly.domain.channelmember;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.domain.channelmember.dto.ChannelMemberDto;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.util.MemberContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ChannelMemberService {
    private final ChannelMemberRepository channelMemberRepository;
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final MemberContext memberContext;
    public ChannelMemberService(
        ChannelMemberRepository channelMemberRepository,
        MemberRepository memberRepository,
        ChannelRepository channelRepository,
        MemberContext memberContext
        ){
        this.channelMemberRepository = channelMemberRepository;
        this.memberRepository = memberRepository;
        this.channelRepository = channelRepository;
        this.memberContext = memberContext;
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
            throw new HttpException(CommonErrorCode.NOT_FOUND, ChannelMember.class, "[" + channelId + ", " + memberId + "]");        
        };
        ChannelMember channelMember = channelMemberRepository.findByChannelAndMember(channel, member).orElse(null);
        return channelMember;
    }

    public List<ChannelMemberDto> getChannelMembersByChannelId(Long cid){
        Channel channel = channelRepository.findById(cid)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));
        List<ChannelMember> channelMember = channelMemberRepository.findByChannel(channel)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND));
        return channelMember.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    public List<ChannelMemberDto> getChannelMembersByMemberId(String mid){
        Member member = memberRepository.findById(mid)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Member.class, mid));
        List<ChannelMember> channelMember = channelMemberRepository.findByMember(member)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND));
        return channelMember.stream()
            .map(ChannelMemberDto::from)
            .toList();
    }

    public List<ChannelMember> createChannelMembersReturnsEntity(Long channel_id, List<String> members_id){
        Channel channel = channelRepository.findById(channel_id)
        .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, channel_id));
        List<Member> members = members_id.stream()
            .map(mid->memberRepository.findById(mid)
            .orElseThrow(() ->new HttpException(CommonErrorCode.NOT_FOUND, ChannelMember.class, mid)))
            .toList();


        List<ChannelMember> created = members.stream()
            .map(member -> channelMemberRepository.save(new ChannelMember(channel, member)))
            .toList();

        if(created==null || created.size()!=members_id.size()) throw new HttpException(CommonErrorCode.SAVE_FAILED, ChannelMember.class, ""); // Dead Code?
        return created;
    }

    public List<ChannelMemberDto> createChannelMembers(Long channel_id, List<String> members_id){
        // 중복 검사(409)
        Channel channel = channelRepository.findById(channel_id).orElse(null);
        if(channel==null){
            throw new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, channel_id);
        }
        members_id.stream().forEach((mid)->{
            Member member = memberRepository.findById(mid).orElseThrow(()-> new HttpException(CommonErrorCode.NOT_FOUND, Member.class, mid));
            if (channelMemberRepository.existsByChannelAndMember(channel, member)){
                throw new HttpException(CommonErrorCode.CONFLICT, ChannelMember.class, "["+channel.getId()+", "+member.getId()+"]"); // Dead Code?
            }
        });

        // 저장
        String requestMemberId = memberContext.getMemberIdFromRequest();
        if(isJoined(channel_id, requestMemberId)==null){ // 채널에 속하지 않은 사용자가 초대 요청한다면
            throw new HttpException(CommonErrorCode.FORBIDDEN); // 요청 거부
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
            if (channelMember==null) throw new HttpException(CommonErrorCode.NOT_FOUND, ChannelMember.class, "["+cid+", "+mid+"]");

            throwExceptionIfNoEditPrivilege(channelMember, cid, mid); // 권한 점검

            channelMember.setRole(role); // 권한 변경

            return ChannelMemberDto.from(channelMember);
        })
        .collect(Collectors.toList());
    }
    
    public void deleteChannelMember(Long cid, String mid) {
        ChannelMember channelMember = isJoined(cid, mid); 
        if (channelMember==null) throw new HttpException(CommonErrorCode.NOT_FOUND, ChannelMember.class, "["+cid+", "+mid+"]");
        throwExceptionIfNoEditPrivilege(channelMember, cid, mid);
        if(channelMember.getRole()==Role.ADMIN){ // 유일한 ADMIN channelMember 삭제는 거부
            boolean isOnlyAdmin = channelMember.getChannel().getChannelMembers().stream()
            .filter(cm->cm.getRole() == Role.ADMIN && !channelMember.getMember().getId().equals(cm.getMember().getId()))
            .findFirst()
            .isEmpty();
            if(isOnlyAdmin) throw new HttpException(CommonErrorCode.CANNOT_DELETE_ADMIN);
        }
        channelMemberRepository.delete(channelMember);
    }

    private void throwExceptionIfNoEditPrivilege(ChannelMember channelMember, Long cid, String mid){
        String requestMemberId = memberContext.getMemberIdFromRequest();
        ChannelMember requestMembersConnection = isJoined(channelMember.getChannel().getId(), requestMemberId);
        if (!channelMember.getMember().getId().equals(requestMemberId) && // 본인이 아니면서
            !(requestMembersConnection!=null && requestMembersConnection.hasPrivilege())){ // 같은 채널의 관리자도 아니면
                throw new HttpException(CommonErrorCode.FORBIDDEN); // 채널 연결 삭제 요청 거부
        }
    }

    // public ChannelMember getChannelMemberFromRequest(Long cid){ // member-channel 연결 반환
    //     String mid = memberContext.getMemberIdFromRequest();
    //     return isJoined(cid, mid);
    // }

}
