package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.entity.file.PostFile;
import com.chatter.Chatly.entity.like.PostLike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String content;
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFile> files = new ArrayList<>();

}
