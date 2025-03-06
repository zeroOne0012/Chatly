package com.chatter.Chatly.entity.like;

import com.chatter.Chatly.entity.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"})) // name = "post_like",  생략
public class PostLike extends Like {
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
