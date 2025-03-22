package com.chatter.Chatly.dto;

import com.chatter.Chatly.entity.Memeber;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDto {
    private String id;
    private String nickname;
    private String email;
    public static MemberDto from(Memeber member) {
        return new MemberDto(member.getId(), member.getNickname(), member.getEmail());
    }
}
