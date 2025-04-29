package com.chatter.Chatly.domain.attachment;

import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public String save(MultipartFile file) {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new HttpException(CommonErrorCode.SAVE_FAILED, Attachment.class, e.getMessage());
        }
    }
}
