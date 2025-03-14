package com.chatter.Chatly.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.entity.file.ArticleFile;
import com.chatter.Chatly.entity.like.ArticleLike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;  // 좋아요 개수
    private List<String> fileUrls;  // 파일 URL 리스트

    
    public static ArticleDto from(Article article) {
        return new ArticleDto(
            article.getId(),
            article.getTitle(),
            article.getContent(),
            article.getCreatedAt(),
            article.getLikes().size(),  // 좋아요 개수
            article.getFiles().stream()
                .map(ArticleFile::getFileUrl)  // 파일 URL 리스트
                .collect(Collectors.toList())
        );
    }
}
