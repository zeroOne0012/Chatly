package com.chatter.Chatly.dto;

import java.time.LocalDateTime;

import com.chatter.Chatly.domain.comment.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Integer likes;
    public static CommentDto from(Comment comment){
        return new CommentDto(
            comment.getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getLikes().size()
        );
    }
}
