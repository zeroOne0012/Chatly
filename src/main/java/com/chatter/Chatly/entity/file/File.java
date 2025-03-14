package com.chatter.Chatly.entity.file;
import java.time.LocalDateTime;

import com.chatter.Chatly.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
public abstract class File {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String fileUrl;
    
    public String getFileUrl() {
        return fileUrl;
    }
    // @Column
    // private LocalDateTime createdAt = LocalDateTime.now();
}