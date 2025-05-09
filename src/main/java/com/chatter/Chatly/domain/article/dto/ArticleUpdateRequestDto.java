package com.chatter.Chatly.domain.article.dto;

import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.domain.article.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor // for ObjectMapper
public class ArticleUpdateRequestDto {
    private String title;
    private String content;
    private List<MultipartFile> files = new ArrayList<>(); // 파일 먼저 업로드
    private List<Long> retainedAttachmentIds; // 수정 없이 유지할 기존 첨부파일 ID 목록

    public Article toEntity() {
        return new Article(title, content);
    }
}