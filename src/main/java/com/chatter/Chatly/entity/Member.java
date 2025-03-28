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
public class Member {
    @Id
    private String id;

    @Column
    private String password; // 암호화
    @Column
    private String nickname;
    @Column(unique = true)
    private String email;
    @Column
    private String profileUrl;

    @OneToMany(mappedBy = "member")
    private Set<Article> articles = new HashSet<>();
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelMember> channels = new HashSet<>();

    @OneToMany(mappedBy="member")
    private Set<Comment> comments = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public Member(String id, String password, String nickname, String email) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = null; // 프로필 사진 기본값 null?
    }
    public void update(Member member){
        this.password = member.password;
        this.nickname = member.nickname;
        this.email = member.email;
        this.profileUrl = member.profileUrl;
    }
}
