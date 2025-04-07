package com.chatter.Chatly.domain.chatroom;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.annotation.CheckAccessPossession;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.dto.ChatRoomDto;
import com.chatter.Chatly.dto.ChatRoomRequestDto;

@RestController
@RequestMapping("/api/channel/{cid}/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    public ChatRoomController(ChatRoomService chatRoomService){
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/all")
    @CheckAccessPossession
    public ResponseEntity<List<ChatRoomDto>> getAllChatRoom(@PathVariable("cid") Long cid){
        List<ChatRoomDto> chatRooms =  chatRoomService.getAllChatRoom(cid);
        return ResponseEntity.ok(chatRooms);
    }
    @PostMapping
    @RequirePrivilege
    public ResponseEntity<ChatRoomDto> createChatRoom(@PathVariable("cid") Long cid, @RequestBody ChatRoomRequestDto dto){
        ChatRoomDto chatRoom =  chatRoomService.createChatRoom(cid, dto);
        return ResponseEntity.ok(chatRoom);
    }
    @PatchMapping("/{id}")
    @RequirePrivilege
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable("cid") Long cid, @PathVariable("id") Long id, @RequestBody ChatRoomRequestDto dto){
        ChatRoomDto chatRoom =  chatRoomService.updateChatRoomid(cid, id, dto);
        return ResponseEntity.ok(chatRoom);
    }
    @DeleteMapping("/{id}")
    @RequirePrivilege
    public ResponseEntity<ChatRoomDto> deleteChatRoom(@PathVariable("cid") Long cid, @PathVariable("id") Long id){
        chatRoomService.deleteChatRoomid(cid, id);
        return ResponseEntity.noContent().build();
    }
}
