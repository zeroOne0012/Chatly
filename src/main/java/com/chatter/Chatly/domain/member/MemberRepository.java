package com.chatter.Chatly.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>{
    boolean existsByEmail(String email);
    @Override
    Optional<Member> findById(String id);
}
