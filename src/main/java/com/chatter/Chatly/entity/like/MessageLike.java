package com.chatter.Chatly.entity.like;

import com.chatter.Chatly.entity.Message;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "message_id"})) // name = "message_like",  생략
public class MessageLike extends Like {
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
}
