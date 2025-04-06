package com.chatter.Chatly.domain.comment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.annotation.CheckAccessPossession;
import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.dto.CommentDto;
import com.chatter.Chatly.dto.CommentRequestDto;

@RestController
@RequestMapping("/api/channel/{cid}/article/{aid}")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    // auth
    // getComments(@AuthenticationPrincipal Member member)

    @GetMapping("/comments") // article id
    @CheckAccessPossession
    public ResponseEntity<List<CommentDto>> getComments(
        @PathVariable("aid") Long aid,
        @RequestParam(required = false, defaultValue = "0", value="page") int page // 0부터 시작!!
        ){
        // @RequestParam(required = false) Long before,
        // @RequestParam(defaultValue = "20") int limit
        List<CommentDto> comments = commentService.getComments(aid, page);

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    @CheckAccessPossession
    public ResponseEntity<CommentDto> createComment(
        @PathVariable("cid") Long cid,
        @PathVariable("aid") Long aid,
        @RequestBody CommentRequestDto requestDto
    ){
        CommentDto comment = commentService.createComment(cid, aid, requestDto);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/comment/{commentId}")
    @RequireOwnership(entityClass = Comment.class, argIdx = 2)
    public ResponseEntity<CommentDto> updateComment(
        @PathVariable("cid") Long cid,
        @PathVariable("aid") Long aid,
        @PathVariable("commentId") Long id,
        @RequestBody CommentRequestDto requestDto
        ){
        CommentDto comment = commentService.updateComment(cid, aid, id, requestDto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comment/{commentId}")
    @RequirePrivilege
    @RequireOwnership(entityClass = Comment.class, argIdx = 2)
    public ResponseEntity<CommentDto> deleteComment(
        @PathVariable("cid") Long cid,
        @PathVariable("aid") Long aid,
        @PathVariable("commentId") Long id
    ){
        commentService.deleteCommet(cid, aid, id);
        return ResponseEntity.noContent().build();
    }
    
}
