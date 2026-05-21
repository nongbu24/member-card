package com.membercard.controller;

import com.membercard.dto.MemberCreateRequest;
import com.membercard.dto.MemberDetailResponse;
import com.membercard.dto.ProfileImageResponse;
import com.membercard.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    // 팀원 정보 저장
    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid MemberCreateRequest request) {
        log.info("[API - LOG] 팀원 정보 저장 API 요청");

        memberService.save(request);

        return ResponseEntity.ok("팀원 정보 저장 성공");
    }

    // 팀원 조회
    @GetMapping("/{id}")
    public ResponseEntity<MemberDetailResponse> getMemberDetail(@PathVariable Long id) {
        log.info("[API - LOG] 팀원 조회 API 요청 - id={}", id);

        MemberDetailResponse response = memberService.findMemberDetail(id);

        return ResponseEntity.ok(response);
    }

    // 프로필 이미지 업로드
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestPart MultipartFile file
    ) {
        log.info("[API - LOG] 팀원 프로필 사진 등록 요청 - id={}", id);

        memberService.uploadProfileImage(id, file);

        return ResponseEntity.ok("프로필 이미지 업로드 성공");
    }

    // 프로필 이미지 조회
    @GetMapping("/{id}/profile-image")
    public ResponseEntity<ProfileImageResponse> getProfileImage(
            @PathVariable Long id
    ) {
        log.info("[API - LOG] 팀원 프로필 사진 조회 요청 - id={}", id);

        ProfileImageResponse response = memberService.getProfileImage(id);

        return ResponseEntity.ok(response);
    }
}
