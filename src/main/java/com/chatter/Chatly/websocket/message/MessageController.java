 package com.chatter.Chatly.websocket.message;

 import org.springframework.messaging.handler.annotation.Header;
 import org.springframework.messaging.handler.annotation.MessageMapping;
 import org.springframework.messaging.handler.annotation.SendTo;
 import org.springframework.stereotype.Controller;

 @Controller
 public class MessageController {
     private final MessageService messageService;

     public MessageController(MessageService messageService){
         this.messageService = messageService;
     }

     @MessageMapping("/chat/send")  // "/app/send"로 온 메시지 처리
     @SendTo("/topic/messages")  // 메시지를 "/topic/messages"로 발행
     public Message sendMessage(Message message, @Header("memberId") String memberId, @Header("chatRoomId") Long chatRoomId){

         Message saved = messageService.saveMessage(message, memberId, chatRoomId);
         System.out.println("Received message: " + message.getMessage());
         return message;  // 받은 메시지를 그대로 반환
     }
 }
