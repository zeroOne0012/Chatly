package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.repository.ArticleRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    
    public List<ArticleDto> getAllArticle() {
        return articleRepository.findAll().stream()
        .map(article -> ArticleDto.from(article))
        .toList();
    }

    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return ArticleDto.from(article);
    }
}
