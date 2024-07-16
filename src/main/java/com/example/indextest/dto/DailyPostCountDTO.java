package com.example.indextest.dto;

import java.time.LocalDate;

/*
 [memberId, date, postCount] -> [작성회원, 작성일자, 작성 게시물 개수]
 */
public record DailyPostCountDTO(Long memberId, LocalDate date, Long postCount) {
}
