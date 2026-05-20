package com.membercard.service;

import com.membercard.dto.MemberCreateRequest;
import com.membercard.dto.MemberDetailResponse;
import com.membercard.entity.Member;
import com.membercard.exception.MemberNotFoundException;
import com.membercard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 팀원 정보 저장
    @Transactional
    public void save(MemberCreateRequest request) {
        Member member = new Member(request.getName(), request.getAge(), request.getMbti());

        memberRepository.save(member);
    }

    // 팀원 조회
    @Transactional(readOnly = true)
    public MemberDetailResponse findMemberDetail(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 팀원입니다.")
        );

        return new MemberDetailResponse(
                member.getId(),
                member.getName(),
                member.getAge(),
                member.getMbti()
        );
    }
}
