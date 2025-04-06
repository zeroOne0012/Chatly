package com.chatter.Chatly.domain.article;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.comment.Comment;
import com.chatter.Chatly.domain.common.Ownable;
import com.chatter.Chatly.domain.entity.File;
import com.chatter.Chatly.domain.entity.Likes;
import com.chatter.Chatly.domain.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화 (@CreatedDate)
public class Article implements Ownable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy="article", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<Comment> comments = new HashSet<>();
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "channel_member_id")
    // private ChannelMember channelMember;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void update(Article article) {
        this.title = article.title;
        this.content = article.content;
        this.files.clear(); // 깊은 복사; 기존 리스트 비우고 새 파일 리스트 추가가
        this.files.addAll(article.files); // 얉은 복사 시 orphanRemoval = true가 설정되어 있으면 자동 삭제?
    }

    @Override
    public String getOwnerId(){
        return member.getId();
    }
}
