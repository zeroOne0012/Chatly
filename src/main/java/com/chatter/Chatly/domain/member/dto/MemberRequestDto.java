package com.chatter.Chatly.domain.member.dto;

import com.chatter.Chatly.domain.member.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {
    private String id;
    private String password;
    private String nickname;
    private String email;

    public Member toEntity() {
        return new Member(id, password, nickname, email);
    }
}
