package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {
    private String id;
    private String password;
    private String nickname;
    private String email;

    public User toEntity() {
        return new User(id, password, nickname, email);
    }
}
