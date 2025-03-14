package com.chatter.Chatly.entity.like;

import com.chatter.Chatly.entity.Article;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "article_id"})) // name = "article_like",  생략
public class ArticleLike extends Like {
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}
