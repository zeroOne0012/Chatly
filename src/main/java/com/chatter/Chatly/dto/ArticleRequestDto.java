package com.chatter.Chatly.dto;

import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.domain.article.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ArticleRequestDto {
    private String title;
    private String content;
    private List<String> fileUrls = new ArrayList<>(); // 파일 먼저 업로드

    public Article toEntity() {
        return new Article(title, content);
    }
}