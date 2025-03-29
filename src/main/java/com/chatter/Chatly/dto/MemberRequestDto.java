package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.Member;

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
