package com.membercard.dto;

import lombok.Getter;

@Getter
public class MemberDetailResponse {
    private final Long id;
    private final String name;
    private final Integer age;
    private final String mbti;

    public MemberDetailResponse(Long id, String name, Integer age, String mbti) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.mbti = mbti;
    }
}
