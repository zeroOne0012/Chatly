package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.entity.Post;
import com.chatter.Chatly.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public List<Post> findAll() {
        return postRepository.findAll();
    }
}
