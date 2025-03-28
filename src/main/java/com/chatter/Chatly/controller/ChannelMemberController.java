package com.chatter.Chatly.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.annotation.CheckPermissionToRead;
import com.chatter.Chatly.annotation.RequireOwnership;
import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.dto.ChannelMemberDto;
import com.chatter.Chatly.dto.ChannelMemberRequestDto;
import com.chatter.Chatly.dto.RoleRequestDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.service.ChannelMemberService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/channel/channel-member")
public class ChannelMemberController {
    private final ChannelMemberService channelMemberService;
    public ChannelMemberController(ChannelMemberService channelMemberService){
        this.channelMemberService = channelMemberService;
    }

    // super Admin?
    @GetMapping("/all")
    public ResponseEntity<List<ChannelMemberDto>> getAllChannelMembers() {
        List<ChannelMemberDto> channelMembers = channelMemberService.getAllChannelMembers();
        return ResponseEntity.ok(channelMembers);
    }

    @GetMapping("/channel/{cid}")
    @CheckPermissionToRead
    public ResponseEntity<List<ChannelMemberDto>> getChannelMembersByChannelId(@PathVariable("cid") Long id) {
        List<ChannelMemberDto> channelMembers = channelMemberService.getChannelMembersByChannelId(id);
        return ResponseEntity.ok(channelMembers);
    }
    @GetMapping("/channel/{cid}/member/{mid}")
    @CheckPermissionToRead
    public ResponseEntity<List<ChannelMemberDto>> getChannelMembersByMemberId(@PathVariable("cid") Long cid, @PathVariable("mid") String id) {
        List<ChannelMemberDto> channelMembers = channelMemberService.getChannelMembersByMemberId(id);
        return ResponseEntity.ok(channelMembers);
    }

    @PostMapping
    public ResponseEntity<List<ChannelMemberDto>> inviteChannelMembers(@RequestBody ChannelMemberRequestDto dto) {
        List<ChannelMemberDto> channelMembers = channelMemberService.createChannelMembers(dto.getChannelId(), dto.getMemberId());
        return ResponseEntity.ok(channelMembers);
    }
    
    @PatchMapping("/{cid}")
    public ResponseEntity<List<ChannelMemberDto>> changeMembersRole(@PathVariable("cid") Long cid, @RequestBody RoleRequestDto dto) {
        List<ChannelMemberDto> channelMembers = channelMemberService.updateChannelMembers(cid, dto.getMemberId(), dto.getRole());
        return ResponseEntity.ok(channelMembers);
    }

    @DeleteMapping("/{cid}/{mid}")
    public ResponseEntity<ChannelMemberDto> kickChannelMember(@PathVariable("cid") Long cid, @PathVariable("mid") String mid) {
        channelMemberService.deleteChannelMember(cid, mid);
        return ResponseEntity.noContent().build();
    }
}