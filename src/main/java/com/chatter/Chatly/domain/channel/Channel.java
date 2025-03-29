package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String channelName;
    @Column
    private String profileUrl = null;

    @OneToMany(mappedBy = "channel")
    private Set<Article> articles = new HashSet<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelMember> channelMembers = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Channel(String channelName){
        this.channelName = channelName;
    }

    public void update(Channel channel){
        this.channelName = channel.channelName;
        // this.profileUrl = channel.profileUrl;
    }
}
