package com.example.service;

import com.example.entity.Member;
import com.example.repository.MemberRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OptimisticLockMemberService {

    private final int MAX_RETRIES = 4;
    private final MemberRepository memberRepository;

    public OptimisticLockMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void increasePopularityWithRetry(Long id, int vote) {
        int attempt = 1;
        while (attempt < MAX_RETRIES) {
            try {
                increasePopularity(id, vote);
                break;
            } catch (OptimisticLockingFailureException ex) {
                System.out.println("재시도 횟수: " + attempt);

                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw ex;
                }
            }
        }
    }

    public void increasePopularity(Long id, int vote) {
        String sessionId = this.memberRepository.getSessionId();
        System.out.println("sessionId = " + sessionId);

        Optional<Member> optMember = this.memberRepository.findByIdWithOptimisticLock(id);
        if (optMember.isPresent()) {
            Member member = optMember.get();
            member.increasePopularity(vote);
        }
    }

}
