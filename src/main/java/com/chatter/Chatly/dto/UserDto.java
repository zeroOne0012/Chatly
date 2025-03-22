package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String id;
    private String nickname;
    private String email;
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getNickname(), user.getEmail());
    }
}
