package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.dto.MemberDto;
import com.chatter.Chatly.dto.MemberRequestDto;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.repository.MemberRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder, AuthService authService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public List<MemberDto> getAllMembers(){
        return memberRepository.findAll().stream()
            .map(MemberDto::from)
            .toList();
    }

    public MemberDto getMemberById(String id){
        Member member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        return MemberDto.from(member);
    }

    public MemberDto createMember(MemberRequestDto dto) {
        // null 확인?
        
        if(memberRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Member already exists with ID: " + dto.getId());
        }
        if(memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Member already exists with Email: " + dto.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Member createdMember = memberRepository.save(new Member(dto.getId(), encodedPassword, dto.getNickname(), dto.getEmail()));
        return MemberDto.from(createdMember);
    }

    public MemberDto updateMember(String id, MemberRequestDto dto){
        if (!isRequester(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        Member member = dto.toEntity();
        Member target = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        target.update(member);
        return MemberDto.from(target);
    }

    public void deleteMember(String id){
        if (!isRequester(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        Member member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        memberRepository.delete(member);
    }

    public boolean isRequester(String targetId){
        String memberId = authService.getMemberIdFromRequest();
        return memberId!=null && memberId.equals(targetId);
    }
}
