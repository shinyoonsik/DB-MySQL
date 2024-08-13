package com.example.indextest.common;

import org.springframework.util.ObjectUtils;

public record CursorRequest(Long key, int size) {
    public static final Long NONE_KEY = -1L;

    public Boolean hasKey(){
        return !ObjectUtils.isEmpty(key);
    }

    public CursorRequest next(Long key){
        return new CursorRequest(key ,size);
    }
}
