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

            return filename;
        } catch (IOException e) {
            throw new HttpException(CommonErrorCode.SAVE_FAILED, Attachment.class, e.getMessage());
        }
    }
    public void delete(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new HttpException(CommonErrorCode.NOT_FOUND, Attachment.class, filename);
            }
        } catch (IOException e) {
            throw new HttpException(CommonErrorCode.IO_EXCEPTION, Attachment.class, e.getMessage());
        }
    }
}
