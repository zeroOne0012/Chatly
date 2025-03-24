package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.dto.ArticleRequestDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
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
        .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));
        return ArticleDto.from(article);
    }

    public ArticleDto createArticle(ArticleRequestDto dto) { // 파일 저장 로직 추가 필요
        if (dto.getTitle()==null || dto.getContent()==null) {
            throw new IllegalArgumentException("Title and content must be provided");
        }
        Article savedArticle = articleRepository.save(dto.toEntity());
        if(savedArticle==null) throw new SaveFailedException("Failed to save article");
        return ArticleDto.from(savedArticle);
    }

    public ArticleDto updateArticle(Long id, ArticleRequestDto requestDto) {
        Article article = requestDto.toEntity();
        Article target = articleRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Article not found with ID: " + id));
        target.update(article);
        return ArticleDto.from(target);
    }
    
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        articleRepository.delete(article); // 영구 삭제 (Hard Delete)
    }

}
