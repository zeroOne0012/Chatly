package com.chatter.Chatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long>{
    
}
