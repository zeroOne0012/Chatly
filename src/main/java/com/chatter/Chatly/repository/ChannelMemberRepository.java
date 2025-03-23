package com.chatter.Chatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chatter.Chatly.entity.ChannelMember;

import io.lettuce.core.dynamic.annotation.Param;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long>{
    Optional<List<ChannelMember>> findByChannel(Long channelId);
    Optional<List<ChannelMember>> findByMember(String memberId);
    // @Query("SELECT cm FROM ChannelMember cm WHERE cm.channel.id = :channelId AND cm.member.id = :memberId")
    // Optional<ChannelMember> findByChannelIdAndMemberId(@Param("channelId") Long channelId, @Param("memberId") String memberId);
}
