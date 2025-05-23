package com.chatter.Chatly.domain.channelmember;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.chatter.Chatly.domain.like.Like;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.domain.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"channel_id", "member_id"})
)
public class ChannelMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "channelMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChannelMember(Channel channel, Member member){
        this.channel = channel;
        this.member = member;
        this.role = Role.USER; 
    }

    public void setRole(Role role){
        this.role = role;
    }
    public boolean hasPrivilege(){
        return this.role==Role.ADMIN || this.role==Role.MODERATOR;
    }
}
