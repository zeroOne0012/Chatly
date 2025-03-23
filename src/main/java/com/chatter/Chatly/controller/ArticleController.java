package com.chatter.Chatly.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.dto.ArticleRequestDto;
import com.chatter.Chatly.service.ArticleService;



@RestController
@RequestMapping("/api/article")
public class ArticleController {
    private final ArticleService articleService;
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ArticleDto>> getAllArticle() {
        List<ArticleDto> articles = articleService.getAllArticle();
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable("id") Long id) {
        ArticleDto article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@RequestBody ArticleRequestDto requestDto) {
        ArticleDto article = articleService.createArticle(requestDto);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("id") Long id, @RequestBody ArticleRequestDto requestDto) {
        ArticleDto article = articleService.updateArticle(id, requestDto);
        return ResponseEntity.ok(article);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDto> deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
