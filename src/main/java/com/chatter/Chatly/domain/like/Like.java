package com.chatter.Chatly.domain.like;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.comment.Comment;
import com.chatter.Chatly.websocket.message.Message;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "entity_type", "entity_id"})
})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_member_id")
    private ChannelMember channelMember;

    @Column(name = "entity_type", nullable = true)
    private String entityType;

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    public Like(String entityType, Long entityId){
        this.entityType = entityType;
        this.entityId=entityId;
    }
}