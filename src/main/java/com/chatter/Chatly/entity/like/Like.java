package com.chatter.Chatly.entity.like;

import java.time.LocalDateTime;

import com.chatter.Chatly.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass

public abstract class Like {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    // @Column
    // private LocalDateTime createdAt = LocalDateTime.now();
}