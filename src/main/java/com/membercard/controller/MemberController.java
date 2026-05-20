package com.membercard.controller;

import com.membercard.dto.MemberCreateRequest;
import com.membercard.dto.MemberDetailResponse;
import com.membercard.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
