package com.chatter.Chatly.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    // normal, assistant, admin
    // ENUM?

    @OneToMany(mappedBy = "role")
    private Set<ChannelMember> channelMember = new HashSet<>();

    // @OneToMany(mappedBy = "role")
    // private Set<Privilege> privileges = new HashSet<>();
    public Role(String name){
        this.name = name;
    }
}
