 package com.chatter.Chatly.websocket.message;

 import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
 import com.chatter.Chatly.domain.member.MemberRepository;
 import com.chatter.Chatly.websocket.message.dto.MessageDto;
 import com.chatter.Chatly.websocket.message.dto.MessageRequestDto;
 import jakarta.transaction.Transactional;
 import lombok.AllArgsConstructor;
 import org.apache.coyote.Response;
 import org.springframework.http.ResponseEntity;
 import org.springframework.messaging.handler.annotation.MessageMapping;
 import org.springframework.messaging.simp.SimpMessagingTemplate;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.*;

 import java.security.Principal;
 import java.util.List;

 @Controller
 @AllArgsConstructor
 public class MessageController {
     private final MessageService messageService;
     private final SimpMessagingTemplate simpMessagingTemplate;
     private final MemberRepository memberRepository;
     private final ChatRoomRepository chatRoomRepository;

     @MessageMapping("/chat/send")  // "/app/send"로 온 메시지 처리
//     @SendTo("/topic/messages")  // retrun 시 메시지를 "/topic/messages"로 발행
     @Transactional
     public void sendMessage(MessageRequestDto dto, Principal principal){
         Message saved = messageService.saveMessage(dto, principal); // entity~dto
         // 채팅방에 메시지 발행
         simpMessagingTemplate.convertAndSend(
                 "/topic/chatroom/" + saved.getChatRoom().getId(),
                 MessageDto.from(saved)
         );
//         return message;  // 받은 메시지를 그대로 반환
     }

     @ResponseBody
     @GetMapping("/api/messages")
     public ResponseEntity<List<MessageDto>> getMessages(
             @RequestParam Long chatRoomId,
             @RequestBody(required = false) Long lastMessageId,
             @RequestParam(defaultValue = "30") int size){

         List<MessageDto> messsages = messageService.getMessages(chatRoomId, lastMessageId, size);
         return ResponseEntity.ok(messsages);
     }

     @ResponseBody
     @PatchMapping("/api/message/{id}")
     public ResponseEntity<MessageDto> updateMessage(@PathVariable("id") Long id, @RequestBody MessageRequestDto dto){
         MessageDto message = messageService.updateMessage(id, dto);
         return ResponseEntity.ok(message);
     }

     @ResponseBody
     @DeleteMapping("/api/chatroom/{chatroom}/message/{id}")
     public ResponseEntity<MessageDto> deleteMessage(@PathVariable("chatroom") Long chatRoomId, @PathVariable("id") Long id){
         messageService.deleteMessage(chatRoomId, id);
         return ResponseEntity.noContent().build();
     }

 }
