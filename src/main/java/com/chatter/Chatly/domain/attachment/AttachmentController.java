package com.chatter.Chatly.domain.attachment;

import com.chatter.Chatly.domain.attachment.dto.AttachmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public AttachmentDto upload(@RequestParam MultipartFile file) {
        return attachmentService.saveFiles(file); // 저장 후 id 반환
    }
}
