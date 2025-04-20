package com.chatter.Chatly.domain.like;

import com.chatter.Chatly.domain.like.dto.LikeDto;
import com.chatter.Chatly.domain.like.dto.LikeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like/{cid}")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping
//    @RequireOwnership(entityClass = ., argIdx = .)
    public ResponseEntity<LikeDto> createLike(@PathVariable("cid") Long cid,@RequestBody LikeRequestDto dto){
        LikeDto like = likeService.createLike(cid, dto);
        return ResponseEntity.ok(like);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<LikeDto> deleteLike(@PathVariable("cid") Long cid, @PathVariable("id") Long id){
        likeService.deleteLike(cid, id);
        return ResponseEntity.noContent().build();
    }
}