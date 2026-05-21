package com.membercard.service;

import com.membercard.dto.ProfileImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public String uploadProfileImage(Long memberId, MultipartFile file) {
        String key = createFileKey(memberId, file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;
        } catch (IOException e) {
            throw new IllegalArgumentException("프로필 이미지 업로드에 실패했습니다.");
        }
    }

    public ProfileImageResponse createPresignedUrl(String key) {
        Instant expiresAt = Instant.now().plus(PRESIGNED_URL_DURATION);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_DURATION)
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();

        return new ProfileImageResponse(presignedUrl, expiresAt);
    }

    // profile-images/1/랜덤값-a.png
    // profile-images/2/랜덤값-b.png
    private String createFileKey(Long memberId, String originalFilename) {
        return "profile-images/"
                + memberId
                + "/"
                + UUID.randomUUID()
                + "-"
                + originalFilename;
    }
}
