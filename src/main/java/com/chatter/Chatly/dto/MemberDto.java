package com.chatter.Chatly.dto;

import com.chatter.Chatly.domain.member.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDto {
    private String id;
    private String nickname;
    private String email;
    public static MemberDto from(Member member) {
        return new MemberDto(member.getId(), member.getNickname(), member.getEmail());
    }
}
