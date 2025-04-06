package com.chatter.Chatly.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.domain.article.Article;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    Page<Comment> findByArticle(Article article, Pageable pageable);
}
