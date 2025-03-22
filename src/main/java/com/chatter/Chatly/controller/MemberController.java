package com.chatter.Chatly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.dto.MemberDto;
import com.chatter.Chatly.dto.MemberRequestDto;
import com.chatter.Chatly.service.MemberService;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    // @GetMapping

    // @PutMapping
    // @DeleteMapping
    @PostMapping("/register")
    public ResponseEntity<MemberDto> registerUser(@RequestBody MemberRequestDto requestDto) {
        MemberDto member = memberService.createMember(requestDto);
        return ResponseEntity.ok(member);
    }
}
