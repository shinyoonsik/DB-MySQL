package com.example.indextest.entity;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Post {
    private final Long id;
    private final Long memberId;
    private final String contents;
    private Long likeCount; // Post엔티티에 likeCount를 넣음으로써 발생하는 핵심점인 비즈니스 로직 문제
    private Long version;
    private final LocalDate createdDate;
    private final LocalDateTime createdAt;

    @Builder
    public Post(Long id, Long memberId, String contents, Long likeCount, Long version, LocalDate createdDate, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(contents);
        this.likeCount = likeCount == null ? 0 : likeCount;
        this.version = version == null ? 0 : version;
        this.createdDate = Objects.isNull(createdDate) ? LocalDate.now() : createdDate;
        this.createdAt = Objects.isNull(createdAt) ? LocalDateTime.now() : createdAt;
    }

    public void incrementLikeCount(){
        this.likeCount += 1;
    }
}
