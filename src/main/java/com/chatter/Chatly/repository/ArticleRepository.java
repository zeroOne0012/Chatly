package com.chatter.Chatly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByIdIn(List<Long> ids);
}
