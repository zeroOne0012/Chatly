package com.chatter.Chatly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByEmail(String email);
    @Override
    Optional<User> findById(String id);
}
