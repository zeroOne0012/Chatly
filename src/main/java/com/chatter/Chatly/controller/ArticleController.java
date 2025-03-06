package com.chatter.Chatly.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.service.ArticleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    public void createArticle(@RequestBody String entity) {
        

    }

    @PatchMapping
    public void updateArticle(@RequestBody String entity) {}
    @DeleteMapping
    public void deleteArticle(@RequestBody String entity) {}
    
    
}
