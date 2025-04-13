package com.chatter.Chatly.domain.chatroom;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.websocket.message.Message;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, columnDefinition = "varchar(255) default chatroom")
    private String name;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel; // 채널에 속한 모든 Member 가 ChatRoom 조회 가능 대상

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatRoom(){
        this.name = "chatroom";
    }
    public ChatRoom(String name){
        this.name = name;
    }
    public void update(ChatRoom chatRoom){
        this.name = chatRoom.name;
    }
}
