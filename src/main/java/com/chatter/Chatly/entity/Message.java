package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.entity.file.MessageFile;
import com.chatter.Chatly.entity.like.MessageLike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String msg;
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "joined_user_id")
    private JoinedUser joinedUser;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageFile> files = new ArrayList<>();
}
