package com.chatter.Chatly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Memeber;

public interface MemberRepository extends JpaRepository<Memeber, String>{
    boolean existsByEmail(String email);
    @Override
    Optional<Memeber> findById(String id);
}
