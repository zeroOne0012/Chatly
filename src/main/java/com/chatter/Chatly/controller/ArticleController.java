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
@RequestMapping("/api/channel/{cid}/article")
public class ArticleController {
    private final ArticleService articleService;
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ArticleDto>> getAllArticle(@PathVariable("cid") Long cid) {
        List<ArticleDto> articles = articleService.getAllArticle(cid);
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable("cid") Long cid, @PathVariable("id") Long id) {
        ArticleDto article = articleService.getArticleById(cid, id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@PathVariable("cid") Long cid, @RequestBody ArticleRequestDto requestDto) {
        ArticleDto article = articleService.createArticle(cid, requestDto);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("cid") Long cid, @PathVariable("id") Long id, @RequestBody ArticleRequestDto requestDto) {
        ArticleDto article = articleService.updateArticle(cid, id, requestDto);
        return ResponseEntity.ok(article);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDto> deleteArticle(@PathVariable("cid") Long cid, @PathVariable("id") Long id) {
        articleService.deleteArticle(cid, id);
        return ResponseEntity.noContent().build();
    }
}
