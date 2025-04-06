package com.chatter.Chatly.domain.entity;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.comment.Comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "article_id"}),
    @UniqueConstraint(columnNames = {"member_id", "comment_id"}),
    @UniqueConstraint(columnNames = {"member_id", "message_id"})
})
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_member_id")
    private ChannelMember channelMember;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = true)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = true)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = true)
    private Message message;
}