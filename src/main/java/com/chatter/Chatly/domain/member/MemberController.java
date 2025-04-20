package com.chatter.Chatly.domain.member;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.domain.member.dto.MemberDto;
import com.chatter.Chatly.domain.member.dto.MemberRequestDto;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        List<MemberDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable("id") String id) {
        MemberDto member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/register")
    public ResponseEntity<MemberDto> registerMember(@RequestBody MemberRequestDto requestDto) {
        MemberDto member = memberService.createMember(requestDto);
        return ResponseEntity.ok(member);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable("id") String id, @RequestBody MemberRequestDto requestDto) {
        MemberDto member = memberService.updateMember(id, requestDto);
        return ResponseEntity.ok(member);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<MemberDto> deleteMember(@PathVariable("id") String id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

}
