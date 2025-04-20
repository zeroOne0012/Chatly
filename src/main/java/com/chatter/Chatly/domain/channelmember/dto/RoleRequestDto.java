package com.chatter.Chatly.domain.channelmember.dto;

import java.util.List;

import com.chatter.Chatly.domain.common.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDto {
    private Role role;
    private List<String> memberId;
}
