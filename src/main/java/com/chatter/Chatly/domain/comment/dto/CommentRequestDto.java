package com.chatter.Chatly.domain.comment.dto;

import com.chatter.Chatly.domain.comment.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private String content;
    public Comment toEntity(){
        return new Comment(content);
    }
}
