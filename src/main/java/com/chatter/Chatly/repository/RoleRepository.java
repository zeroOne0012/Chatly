package com.chatter.Chatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
}
