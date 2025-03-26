package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

    @OneToMany(mappedBy = "channelMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Role> role = new HashSet<>();  // 속한 Channel에서의 Member 역할(권한들)

    // @OneToMany(mappedBy = "channelMember")
    // private Set<Article> article = new HashSet<>(); 

    // @OneToMany(mappedBy = "channelMember")
    // private Set<Comment> comment = new HashSet<>(); 

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChannelMember(Channel channel, Member member){
        this.channel = channel;
        this.member = member;
    }
}
