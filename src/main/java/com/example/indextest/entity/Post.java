package com.example.indextest.entity;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


// 문제: likeCount를 Post에 셋팅함으로써 발생하는 문제
// 1. 같은 회원의 likeCount 중복체크가 번거롭다
// 2. 락을 사용해서 발생하는 성능상의 문제
//      -> 1) 배타적락을 사용하면 락을 통해 요청이 순차적으로 처리되기때문에 병목이 발생할 여지가 크다.
//      -> 2) 낙관적락을 사용했다면 끊임없이 요청이 실패할 것이다.
// Sol1) likeCount를 다른 테이블로 분리함으로써 성능 병목지점을 해소하고 더불어 회원 정보를 같이 저장함으로써 회원마다 좋아요 게시글을 모아서 볼수있는 기능도 쉽게 구현할 수 있다. + 아래의 테이블은 제3정규화를 위반한 테이블. 따라서 제3정규형으로 변경
//       PostDTO조회시 PostLike테이블의 count조회를 해야 한다 -> 기존 lock방식은 쓰기를 희생했는데! 지금은 조회를 희생 => 트레이드 오프;
// Sol2) Post테이블에 likeCount를 그대로 두고 스케줄러를 돌려 특정 주기마다 PostLike테이블 count()를 Post테이블의 likeCount에 넣어주는 전략! -> likeCount를 Post테이블에 캐싱해놓는 전략
// Sol3) Redis를 통해 likeCount를 관리
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
