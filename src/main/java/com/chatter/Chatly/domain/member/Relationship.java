package com.chatter.Chatly.domain.member;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chatter.Chatly.domain.common.FriendState;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member1_id", nullable=false)
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2_id", nullable=false)
    private Member member2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendState state;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}