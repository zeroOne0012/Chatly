package com.chatter.Chatly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.entity.file.ArticleFile;
import com.chatter.Chatly.entity.like.ArticleLike;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleFile> files = new ArrayList<>();

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void update(Article article) {
        this.title = article.title;
        this.content = article.content;
        this.files.clear(); // 깊은 복사; 기존 리스트 비우고 새 파일 리스트 추가가
        this.files.addAll(article.files); // 얉은 복사 시 orphanRemoval = true가 설정되어 있으면 자동 삭제?
    }
}
