package com.membercard.service;

import com.membercard.dto.MemberCreateRequest;
import com.membercard.dto.MemberDetailResponse;
import com.membercard.dto.ProfileImageResponse;
import com.membercard.entity.Member;
import com.membercard.exception.MemberNotFoundException;
import com.membercard.exception.ProfileImageNotFoundException;
import com.membercard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

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

    @Transactional
    public void uploadProfileImage(Long id, MultipartFile file) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 팀원입니다.")
        );

        String profileImageKey = s3Service.uploadProfileImage(id, file);
        member.updateProfileImageKey(profileImageKey);
    }

    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImage(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 팀원입니다.")
        );

        if (member.getProfileImageKey() == null) {
            throw new ProfileImageNotFoundException("등록된 프로필 이미지가 없습니다.");
        }

        return s3Service.createPresignedUrl(member.getProfileImageKey());
    }
}
