package com.chatter.Chatly.domain.article;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chatter.Chatly.annotation.CheckAccessPossession;
import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.domain.article.dto.ArticleDto;
import com.chatter.Chatly.domain.article.dto.ArticleRequestDto;
import com.chatter.Chatly.domain.common.dto.TargetsDto;



@RestController
@RequestMapping("/api/channel/{cid}/article")
public class ArticleController {
    private final ArticleService articleService;
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    @CheckAccessPossession
    public ResponseEntity<List<ArticleDto>> getAllArticle(@PathVariable("cid") Long cid) {
        List<ArticleDto> articles = articleService.getAllArticle(cid);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/mine")
    @CheckAccessPossession
    public ResponseEntity<List<ArticleDto>> getAllArticleByMember(@PathVariable("cid") Long cid) {
        List<ArticleDto> articles = articleService.getAllArticleByMember(cid);
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/{id}")
    @CheckAccessPossession
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable("cid") Long cid, @PathVariable("id") Long id) {
        ArticleDto article = articleService.getArticleById(cid, id);
        return ResponseEntity.ok(article);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CheckAccessPossession
    public ResponseEntity<ArticleDto> createArticle(@PathVariable("cid") Long cid, @ModelAttribute ArticleRequestDto requestDto) {
        System.out.println("Uploaded files: " + requestDto.getFiles().size());
        System.out.println("testetsetstetsetdebugdebug: " + requestDto.getTitle() + " tttt " + requestDto.getContent());
        ArticleDto article = articleService.createArticle(cid, requestDto);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    // @RequirePrivilege
    @RequireOwnership(entityClass = Article.class, argIdx = 1)
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("cid") Long cid, @PathVariable("id") Long id, @RequestBody ArticleRequestDto requestDto) {
        ArticleDto article = articleService.updateArticle(cid, id, requestDto);
        return ResponseEntity.ok(article);

    }

    @DeleteMapping
    @RequirePrivilege
    @RequireOwnership(entityClass = Article.class, argIdx = 1)
    public ResponseEntity<ArticleDto> deleteArticle(@PathVariable("cid") Long cid, @RequestBody TargetsDto ids) {
        articleService.deleteArticle(cid, ids);
        return ResponseEntity.noContent().build();
    }
}
