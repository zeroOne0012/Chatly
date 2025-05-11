package com.chatter.Chatly.domain.article.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.attachment.Attachment;

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
    private Long likes;  // 좋아요 개수
    private String memberId;
    private List<String> fileUrls;  // 파일 URL 리스트
    private List<Long> attachmentIds; // 파일 목록 수정 요청에 사용될 Attachment Id List

    
    public static ArticleDto from(Article article) {
        List<Attachment> files = Optional.ofNullable(article.getFiles()).orElse(List.of()); // file null 방지
        return new ArticleDto(
            article.getId(),
            article.getTitle(),
            article.getContent(),
            article.getCreatedAt(),
            article.getLikes(),  // 좋아요 개수
            article.getMember().getId(),
            files.stream()
                .map(Attachment::getFileUrl)  // 파일 URL 리스트
                .collect(Collectors.toList()),
            files.stream()
                .map(Attachment::getId)
                .collect(Collectors.toList())
        );
    }
}
