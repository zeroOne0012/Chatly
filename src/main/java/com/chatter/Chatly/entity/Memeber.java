package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class Memeber {
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

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMember> channels = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Memeber(String id, String password, String nickname, String email) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = null; // 프로필 사진 기본값 null?
    }
    public void update(Memeber member){
        this.password = member.password;
        this.nickname = member.nickname;
        this.email = member.email;
        this.profileUrl = member.profileUrl;
    }
}
