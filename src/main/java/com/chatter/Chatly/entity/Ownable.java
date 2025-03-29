package com.chatter.Chatly.entity;

// Member를 가진 객체 - MemberId 반환
public interface Ownable<T> {
    T getOwnerId();
}
