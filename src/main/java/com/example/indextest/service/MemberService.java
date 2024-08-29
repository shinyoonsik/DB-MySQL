package com.example.indextest.service;

import com.example.indextest.entity.Member;
import com.example.indextest.entity.MemberHistory;
import com.example.indextest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
    public class MemberService {

    private final MemberRepository memberRepository;


    public Member create(Member memberDTO){
        return getMember(memberDTO);
    }

    @Transactional
    public Member getMember(Member memberDTO) {
        Member newMember = Member.builder()
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .build();


        Member savedMember = this.memberRepository.save(newMember);

        var a = 3 / 0;

        saveMemberHistory(savedMember);

        return savedMember;
    }

    private void saveMemberHistory(Member savedMember) {
        MemberHistory memberHistory = MemberHistory.builder()
                .memberId(savedMember.getId())
                .nickname(savedMember.getNickname())
                .build();

        this.memberRepository.save(memberHistory);
    }


}
