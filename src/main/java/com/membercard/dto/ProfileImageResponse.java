package com.membercard.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ProfileImageResponse {
    private final String presignedUrl;
    private final Instant expiresAt;

    public ProfileImageResponse(String presignedUrl, Instant expiresAt) {
        this.presignedUrl = presignedUrl;
        this.expiresAt = expiresAt;
    }
}
