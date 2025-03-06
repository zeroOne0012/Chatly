package com.chatter.Chatly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();
}
