package com.chatter.Chatly.domain.article.dto;

import java.util.ArrayList;
import java.util.List;

import com.chatter.Chatly.domain.article.Article;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor // for ObjectMapper
public class ArticleRequestDto {
    private String title;
    private String content;
//    private List<MultipartFile> files = new ArrayList<>();

    public Article toEntity() {
        return new Article(title, content);
    }
}