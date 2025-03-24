package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
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
        Member member = dto.toEntity();
        Member target = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        target.update(member);
        return MemberDto.from(target);
    }

    public void deleteMember(String id){
        Member member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        memberRepository.delete(member);
    }
}
