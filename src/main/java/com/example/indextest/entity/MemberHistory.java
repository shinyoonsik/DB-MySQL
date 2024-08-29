package com.example.indextest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberHistory {
    private Long id;
    private Long memberId;
    private String nickname;

    @CreatedDate
    private LocalDateTime createdAt;
}
