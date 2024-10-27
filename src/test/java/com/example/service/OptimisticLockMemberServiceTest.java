package com.example.service;

import com.example.entity.Member;
import com.example.repository.MemberRepository;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockMemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OptimisticLockMemberService optimisticLockMemberService;


    @BeforeEach
    void init() {
        Member member = this.memberRepository.save(new Member(1L, "신룡이", 0, 0L));
    }

    @AfterEach
    void deleteAll() {
        this.memberRepository.deleteAll();
    }

    @Test
    void 낙관적락_테스트_with_싱글스레드() {
        List<Member> members = this.memberRepository.findAll();
        Member member = members.get(0);

        this.optimisticLockMemberService.increasePopularityWithRetry(member.getId(), 1);

        Optional<Member> optMember = this.memberRepository.findById(member.getId());
        optMember.ifPresent(myMember -> {
            System.out.println("myMember = " + myMember);
            assertEquals(1, myMember.getPopularity());
        });
    }

    // TODO 낙관적락 멀티 스레드 테스트
    // TODO 재시도 로직과 비즈니스 로직 분리 -> facade패턴 or class: OptimisticLockRetryTemplate
    // TODO @Transactional 추가 정리(https://chatgpt.com/c/9d07ef2e-89fd-4c65-8379-dd60aacf1121) -> notion
}