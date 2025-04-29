package com.chatter.Chatly.domain.attachment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileUrl;

    private String fileName;

    private String contentType;

    private Long size;

    // 다형적 연관 관계
    @Column(nullable = false)
    private String entityType; // "ARTICLE", "COMMENT", "MESSAGE" 등

    @Column(nullable = false)
    private Long entityId;
}