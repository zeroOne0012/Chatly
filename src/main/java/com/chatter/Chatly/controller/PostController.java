package com.chatter.Chatly.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chatter.Chatly.entity.Post;
import com.chatter.Chatly.service.PostService;


@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Post>> getMethodName() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }
    
}
