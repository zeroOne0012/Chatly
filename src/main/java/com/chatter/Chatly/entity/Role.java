package com.chatter.Chatly.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Role {
    @Id
    private Long id;
    @Column
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<Privilege> privileges = new HashSet<>();
}
