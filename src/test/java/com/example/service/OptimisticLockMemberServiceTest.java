package com.example.service;

import com.example.entity.Member;
import com.example.facade.OptimisticLockMemberFacade;
import com.example.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockMemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OptimisticLockMemberFacade optimisticLockMemberFacade;


    @BeforeEach
    void init() {
        Member member = this.memberRepository.save(new Member(1L, "신룡이", 0, 0L));
    }

    @AfterEach
    void deleteAll() {
        this.memberRepository.deleteAll();
    }

    @Test
    void 낙관적락_테스트_with_싱글스레드() throws InterruptedException {
        List<Member> members = this.memberRepository.findAll();
        Member member = members.get(0);

        this.optimisticLockMemberFacade.increasePopularityWithRetry(member.getId(), 1);

        Optional<Member> optMember = this.memberRepository.findById(member.getId());
        optMember.ifPresent(myMember -> {
            System.out.println("myMember = " + myMember);
            assertEquals(1, myMember.getPopularity());
        });
    }

    // TODO 낙관적락 멀티 스레드 테스트
    // TODO 재시도 로직과 비즈니스 로직 분리 -> facade패턴 or class: OptimisticLockRetryTemplate
    // TODO @Transactional 추가 정리(https://chatgpt.com/c/9d07ef2e-89fd-4c65-8379-dd60aacf1121) -> notion
    // TODO JPA는 1차 캐시인 영속성 컨텍스트에서 엔티티를 관리해주므로 조회쿼리에 OPTIMISTIC을 붙히면 더티 체킹으로 업데이트시 조회 대상에 대한 버저닝을 JPA가 해주는 건가?


    @Test
    void 낙관적란_테스트_with_멀티스레드() throws InterruptedException {
        List<Member> members = this.memberRepository.findAll();
        Member member = members.get(0);

        int reqCount = 100;
        int expected = 100;
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(reqCount);

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        for (int i = 0; i < reqCount; i++) {
            executor.submit(() -> {
                try {
                    this.optimisticLockMemberFacade.increasePopularityWithRetry(member.getId(), 1);
                } catch (InterruptedException e) {
                    System.out.println("테스트: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        stopWatch.stop();

        System.out.println("stopWatch.getTotalTime() = " + stopWatch.getTotalTime(TimeUnit.SECONDS));

        Optional<Member> optMember = this.memberRepository.findById(member.getId());
        assertTrue(optMember.isPresent());
        assertThat(optMember.get().getPopularity()).isEqualTo(expected);
    }

}