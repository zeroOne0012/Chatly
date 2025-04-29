package com.chatter.Chatly.domain.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class AttachmentRequestDto {
    private String entityType;
    private Long entityId;
    private List<MultipartFile> files;
}
