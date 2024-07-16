package com.example.indextest.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record DailyPostCountReqDTO(
        Long memberId,

        LocalDate firstDate,
        LocalDate lastDate) {
}
