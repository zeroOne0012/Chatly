package com.chatter.Chatly.domain.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.common.Ownable;
import com.chatter.Chatly.domain.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class Comment implements Ownable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Column
    private Long likes;

    public Comment(String content){
        this.content = content;
        this.likes = 0L;
    }

    public void update(Comment comment){
        this.content = comment.content;
    }

    @Override
    public String getOwnerId(){
        return member.getId();
    }
}
