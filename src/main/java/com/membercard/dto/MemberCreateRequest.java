package com.membercard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberCreateRequest {
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @NotNull(message = "나이를 입력해 주세요.")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    private Integer age;

    @NotNull(message = "MBTI를 입력해 주세요.")
    @Pattern(
            regexp = "^(ISTJ|ISFJ|INFJ|INTJ|ISTP|ISFP|INFP|INTP|ESTP|ESFP|ENFP|ENTP|ESTJ|ESFJ|ENFJ|ENTJ)$",
            message = "올바른 MBTI를 입력해 주세요."
    )
    private String mbti;
}
