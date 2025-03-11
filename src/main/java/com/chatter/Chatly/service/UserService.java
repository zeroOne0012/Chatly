package com.chatter.Chatly.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.UserDto;
import com.chatter.Chatly.dto.UserRequestDto;
import com.chatter.Chatly.entity.User;
import com.chatter.Chatly.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(UserRequestDto dto) {
        // null 확인 로직 필요
        
        if(userRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("User already exists with ID: " + dto.getId());
        }
        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("User already exists with Email: " + dto.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User createdUser = userRepository.save(new User(dto.getId(), encodedPassword, dto.getNickname(), dto.getEmail()));
        return UserDto.from(createdUser);
    }
}
