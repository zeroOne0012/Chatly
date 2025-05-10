package com.chatter.Chatly.websocket.message.dto;

import com.chatter.Chatly.websocket.message.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRequestDto {
    private String message;
    @JsonProperty("chatroom")
    private Long chatRoomId;
//    @JsonProperty("files")
    private List<MultipartFile> files;
    private List<Long> retainedAttachmentIds; // 수정 없이 유지할 기존 첨부파일 ID 목록

}
