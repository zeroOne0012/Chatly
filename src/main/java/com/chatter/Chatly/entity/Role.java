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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "channel_member_id")
    private ChannelMember channelMember;

    @OneToMany(mappedBy = "role")
    private Set<Privilege> privileges = new HashSet<>();
}
