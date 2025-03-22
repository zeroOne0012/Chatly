package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class User {
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
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinedUser> channels = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User(String id, String password, String nickname, String email) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = null; // 프로필 사진 기본값 null?
    }
    public void update(User user){
        this.password = user.password;
        this.nickname = user.nickname;
        this.email = user.email;
        this.profileUrl = user.profileUrl;
    }
}
