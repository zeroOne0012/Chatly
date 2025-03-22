package com.chatter.Chatly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Privilege {
    @Id
    private Long id;
    @Column
    private String privilegeName;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;
}
