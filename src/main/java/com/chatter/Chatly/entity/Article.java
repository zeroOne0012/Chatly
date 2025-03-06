package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.entity.file.ArticleFile;
import com.chatter.Chatly.entity.like.ArticleLike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String content;
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleFile> files = new ArrayList<>();

}
