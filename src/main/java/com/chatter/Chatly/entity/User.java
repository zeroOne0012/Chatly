package com.chatter.Chatly.entity;

import java.util.ArrayList;
import java.util.List;

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
    private String password;
    @Column
    private String nickname;
    @Column(unique = true)
    private String email;
    @Column
    private String profileUrl;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinedUser> channels = new ArrayList<>();
}
