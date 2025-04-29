package com.chatter.Chatly.websocket.message.dto;

import com.chatter.Chatly.domain.attachment.Attachment;
import com.chatter.Chatly.websocket.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private String memberId;
    private Long chatRoomId;
    private List<String> files;

    public static MessageDto from(Message message){
        return new MessageDto(
                message.getId(),
                message.getMessage(),
                message.getCreatedAt(),
                message.getMember().getId(),
                message.getChatRoom().getId(),
                message.getFiles() == null ? null : new ArrayList<>(message.getFiles().stream().map(Attachment::getFileUrl).toList()) // 깊은 복사
        );
    }
}
