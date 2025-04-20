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
@Table(name = "likes",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"channel_member_id", "entity_type", "entity_id"})
})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_member_id", nullable = false )
    private ChannelMember channelMember;

    @Column(name = "entity_type", nullable = false )
    private String entityType;

    @Column(name = "entity_id", nullable = false )
    private Long entityId;

    public Like(ChannelMember channelMember, String entityType, Long entityId){
        this.channelMember = channelMember;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}