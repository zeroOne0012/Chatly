package com.chatter.Chatly.domain.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    public List<Attachment> saveFiles(String entityType, Long entityId, List<MultipartFile> files) {

        if (files == null) return List.of();
        return files.stream().map(file -> {
            String url = fileStorageService.save(file);
            Attachment att = new Attachment();
            att.setEntityType(entityType);
            att.setEntityId(entityId);
            att.setFileUrl(url);
            att.setFileName(file.getOriginalFilename());
            att.setContentType(file.getContentType());
            att.setSize(file.getSize());
            return attachmentRepository.save(att);
        }).toList();
    }

    public void deleteByEntity(String entityType, Long entityId) {
        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
        attachments
            .forEach(attachment -> {
                fileStorageService.delete(attachment.getFileName());
            });
        attachmentRepository.deleteAll(attachments);
    }
}
