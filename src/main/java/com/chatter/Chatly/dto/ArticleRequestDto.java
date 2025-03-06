package com.chatter.Chatly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ArticleRequestDto {
    private Long id;
    private String title;
    private String content;
    private List<String> fileUrls; // 파일 먼저 업로드
}