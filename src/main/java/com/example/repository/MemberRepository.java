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

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithOptimisticLock(Long id);
}
