package com.example.service;

import com.example.entity.Member;
import com.example.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OptimisticLockMemberService {

    private final MemberRepository memberRepository;

    public OptimisticLockMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 트랜잭션이 끝날때 flush()가 호출되어 이때 더티 체킹을 통해 만들어진 update쿼리가 전송된다.
    @Transactional
    public Member increasePopularity(Long id, int vote) {
        String sessionId = this.memberRepository.getSessionId();
        System.out.println("sessionId = " + sessionId);

        Optional<Member> optMember = this.memberRepository.findByIdWithOptimisticLock(id);
        return optMember.map(member -> {
           member.increasePopularity(vote);
           return member;
        }).orElseThrow(() -> new NoSuchElementException("Member with ID " + id + " not found"));
    }

}
