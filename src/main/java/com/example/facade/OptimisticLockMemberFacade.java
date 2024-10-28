package com.example.facade;

import com.example.service.OptimisticLockMemberService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class OptimisticLockMemberFacade {

    private final int MAX_RETREIES = 15;
    private final OptimisticLockMemberService optimisticLockMemberService;

    public OptimisticLockMemberFacade(OptimisticLockMemberService optimisticLockMemberService) {
        this.optimisticLockMemberService = optimisticLockMemberService;
    }

    public void increasePopularityWithRetry(Long id, int vote) throws InterruptedException {
        int attempt = 0;
        while (attempt < MAX_RETREIES) {
            System.out.println("현재 Thread: " + Thread.currentThread().getName() + " attempt = " + attempt);
            try{
                this.optimisticLockMemberService.increasePopularity(id, vote);
                break;
            }catch (OptimisticLockingFailureException ex){
                System.out.println("현재 Thread: " + Thread.currentThread().getName() + " OptimisticLockMemberFacade -> " + ex.getMessage());
                Thread.sleep(200); // 의도적으로 요청을 늦춤으로서( == 동시성 출돌을 줄여), MAX_RETREIES안에 성공 가능성을 높힌다
                attempt++;

                if(attempt > MAX_RETREIES) throw new RuntimeException("increasePopularityWithRetry 실패!");
            }
        }
    }
}
