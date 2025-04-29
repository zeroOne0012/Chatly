package com.chatter.Chatly.domain.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentDto {
    private Long id;
    private String fileUrl;
    private String originalFilename;
    private String contentType;
    private Long size;
}
