package com.chatter.Chatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.entity.Channel;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long>{
    Optional<List<ChannelMember>> findByChannel(Channel channel);
    Optional<List<ChannelMember>> findByMember(Member member);
    Optional<ChannelMember> findByChannelAndMember(Channel channel, Member member);
}
