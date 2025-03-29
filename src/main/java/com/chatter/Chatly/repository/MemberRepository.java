package com.chatter.Chatly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatter.Chatly.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String>{
    boolean existsByEmail(String email);
    @Override
    Optional<Member> findById(String id);
}
