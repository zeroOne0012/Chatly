package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.Memeber;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRequestDto {
    private String id;
    private String password;
    private String nickname;
    private String email;

    public Memeber toEntity() {
        return new Memeber(id, password, nickname, email);
    }
}
