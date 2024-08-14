package com.example.indextest.common;

import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/*
 createdAt이 cursor-key의 역할을 함
 */
public record CursorRequestByCreatedDate(LocalDateTime createdAt, int size) {

    public static final LocalDateTime NONE_KEY = null;
    public Boolean hasKey() {
        return !ObjectUtils.isEmpty(createdAt);
    }

    public CursorRequestByCreatedDate next(LocalDateTime createdAt){
        return new CursorRequestByCreatedDate(createdAt, size);
    }
}
