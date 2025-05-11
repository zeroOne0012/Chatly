package com.chatter.Chatly.domain.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    // attachments 개수 제한?

    // attachment를 포함하는 entity GET에서 수동적으로 Attachment 불러와줘야 함(다형성 구조)


    public List<Attachment> saveFiles(String entityType, Long entityId, List<MultipartFile> files) {

        if (files == null) return List.of();
        return files.stream().map(file -> {
            String uuidFileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 파일 이름 중복 방지 O

            String url = fileStorageService.save(file, uuidFileName);
            Attachment att = new Attachment();
            att.setEntityType(entityType);
            att.setEntityId(entityId);
            att.setFileUrl(url);
            att.setFileName(uuidFileName);
            att.setContentType(file.getContentType());
            att.setSize(file.getSize());
            return attachmentRepository.save(att);
        }).toList();
    }



    public List<Attachment> updateFiles(String entityType, Long entityId, List<MultipartFile> newFiles, List<Long> retainedAttachmentIds){
        // 기존 파일 중 삭제할 파일 삭제
        deleteByEntity(entityType, entityId, retainedAttachmentIds);
        // 새로운 파일 저장 -> list 반환(새 파일만 반환)
        // !!!!!! 메서드 실행 후 entity(((Article or Message))).getFiles().addAll(newAttachments); 필수
        return saveFiles(entityType, entityId, newFiles);
    }



    public void deleteByEntity(String entityType, Long entityId) {
        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
        attachments
            .forEach(attachment -> {
                fileStorageService.delete(attachment.getFileUrl());
            });
        attachmentRepository.deleteAll(attachments);
    }

    public void deleteByEntity(String entityType, Long entityId, List<Long> retainedAttachmentIds) {
        if(retainedAttachmentIds==null){ // 유지 대상 리스트가 없으면 전체 삭제
            deleteByEntity(entityType, entityId);
            return;
        }
        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
        attachments
                .forEach(attachment -> {
                    if(!retainedAttachmentIds.contains(attachment.getId())) // 유지 대상이 아니면 삭제
                        fileStorageService.delete(attachment.getFileUrl());
                });
        attachmentRepository.deleteAll(attachments);
    }
}
