package com.chatter.Chatly.domain.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TargetsDto {
    private List<Long> lst;
}
