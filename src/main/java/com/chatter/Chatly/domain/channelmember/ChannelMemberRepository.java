package com.chatter.Chatly.domain.channelmember;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.member.Member;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long>{
    Optional<List<ChannelMember>> findByChannel(Channel channel);
    Optional<List<ChannelMember>> findByMember(Member member);
    Optional<ChannelMember> findByChannelAndMember(Channel channel, Member member);
    boolean existsByChannelAndMember(Channel channel, Member member);
}
