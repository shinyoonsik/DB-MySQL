package com.example.repository;

import com.example.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select connection_id()")
    String getSessionId();


    // TODO JPA는 1차 캐시인 영속성 컨텍스트에서 엔티티를 관리해주므로 조회쿼리에 OPTIMISTIC을 붙히면 더티 체킹으로 업데이트시 조회 대상에 대한 버저닝을 JPA가 해주는 건가?
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithOptimisticLock(Long id);
}
