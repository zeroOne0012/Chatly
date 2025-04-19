package com.chatter.Chatly.domain.like;

import com.chatter.Chatly.annotation.CheckAccessPossession;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.domain.chatroom.ChatRoomService;
import com.chatter.Chatly.domain.like.dto.LikeDto;
import com.chatter.Chatly.domain.like.dto.LikeRequestDto;
import com.chatter.Chatly.dto.ChatRoomDto;
import com.chatter.Chatly.dto.ChatRoomRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/like/{cid}")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping
    public ResponseEntity<LikeDto> createLike(@PathVariable("cid") Long cid, LikeRequestDto dto){
        LikeDto like = likeService.createLike(cid, dto);
        return ResponseEntity.ok(like);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<LikeDto> deleteLike(@PathVariable("cid") Long cid, @PathVariable("id") Long id){
        LikeDto like = likeService.deleteLike(cid, id);
        return ResponseEntity.ok(like);
    }
}