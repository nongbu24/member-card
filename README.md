# Member Card

팀원 정보를 저장하고 조회할 수 있는 Spring Boot 기반 API 서버입니다.  
AWS EC2, RDS, Parameter Store, S3를 사용하여 배포 환경을 구성했습니다.

## LV 0. AWS Budget 설정

클라우드 실습 중 비용 초과를 방지하기 위해 AWS Budgets를 설정했습니다.

### 설정 내용

- 월 예산: `$100`
- 알림 조건: 예산의 `80%` 도달 시 이메일 알림 발송

### AWS Budgets 설정 화면

![AWS Budget 설정 화면](img.png)

## LV 1. 네트워크 구축 및 핵심 기능 배포

Spring Boot 애플리케이션을 EC2에 배포하고 실행했습니다.

### EC2 Public IP

```text
3.35.18.204
```

## LV 2. DB 분리 및 보안 연결

### RDS 구축

로컬 테스트 이후 실제 배포 환경에서는 AWS RDS MySQL을 사용하도록 구성했습니다.  
Spring Boot 애플리케이션은 EC2에서 실행되며, RDS에 연결하여 회원 정보를 저장하고 조회합니다.

### Parameter Store 설정

DB 접속 정보와 확인용 파라미터는 코드에 직접 작성하지 않고 AWS Systems Manager Parameter Store에 저장했습니다.

저장한 파라미터는 다음과 같습니다.

- `/config/member-card/url`
- `/config/member-card/username`
- `/config/member-card/password`
- `/config/member-card/team-name`
- `/config/member-card/s3-bucket-name`

Spring Boot 실행 시 `prod` profile에서 Parameter Store 값을 주입받아 RDS와 S3 설정에 사용합니다.

### Actuator Info 검증

Parameter Store에 저장한 `team-name` 값을 `/actuator/info` 엔드포인트에서 확인할 수 있도록 설정했습니다.

Actuator Info 엔드포인트 URL:

```text
http://3.35.18.204:8080/actuator/info
```

응답 예시:

```json
{
  "team-name": "ec2"
}
```

### RDS 보안 그룹 설정

RDS 보안 그룹의 인바운드 규칙에는 직접 IP 주소를 등록하지 않고, EC2의 보안 그룹 ID를 Source로 등록했습니다.  
이를 통해 EC2에서만 RDS에 접근할 수 있도록 보안 그룹 체이닝을 구성했습니다.

아래 스크린샷에서 RDS 보안 그룹 인바운드 규칙의 Source가 `0.0.0.0/0`이 아닌 EC2 보안 그룹 ID로 설정되어 있음을 확인할 수 있습니다.

<img width="1672" height="491" alt="RDS 보안 그룹 인바운드 규칙" src="https://github.com/user-attachments/assets/aef6415f-fe6d-4be3-9ef2-ed874e480865" />

---

## LV 3. 프로필 사진 기능 추가와 권한 관리

### S3 버킷 생성

프로필 이미지는 EC2 서버 디스크에 저장하지 않고 S3 버킷에 업로드하도록 구현했습니다.  
S3 버킷은 모든 퍼블릭 액세스 차단 설정을 켠 상태로 생성했습니다.

### IAM Role 및 권한 설정

Access Key를 코드에 직접 작성하지 않고, EC2에 연결된 IAM Role을 통해 S3에 접근하도록 구성했습니다.

EC2 IAM Role에는 다음 S3 권한을 부여했습니다.

- `s3:PutObject`
- `s3:GetObject`

애플리케이션은 IAM Role 권한을 사용하여 S3에 이미지를 업로드하고, 다운로드용 Presigned URL을 생성합니다.

### API

프로필 이미지 업로드:

```http
POST /api/members/{id}/profile-image
Content-Type: multipart/form-data
```

요청 Body:

```text
key: file
type: File
```

프로필 이미지 조회:

```http
GET /api/members/{id}/profile-image
```

응답 예시:

```json
{
    "presignedUrl": "https://member-card-profile-images-garden245.s3.ap-northeast-2.amazonaws.com/profile-images/1/446d230f-fc29-4ef5-84ec-630e31d9b3fc-IMG_2781.jpeg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEJ%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgDYbtrJbETfStO962413%2F0hgyrD3DLrdIiB1JJNa5uooCIBqu5yZqhUFd7Unu5WffxjPfmvNtTDzZuoBFU7w4AvNhKskFCGgQABoMODgwMjUyOTc0OTE5IgxaySKmn1g7blr%2FWsoqpgUc9jMz3w18H%2FoPmm4dPN5vsR8zvxUEKTC8jzIGebepIRrMkSC%2B%2F334wqfWo9FsWeAnyM0vkoxFw964U6%2FeoqYTeGMB6NRecZU70E%2FeteCCcYOjxDAXaDprVXeUPH2H6enjoLnHpBg5ZVtBMCwjDKzEcCMi6SZ4dDcNSoX%2FgsIAVMHfGuFX6y9K7XK7YEG1YGM9DYHHSM2AltdeLDYi6Wxm7%2BD1q75ioxnpDOKVqsord1kU82oa7vB63dRwn90jR583JkeRgSlM32ZxT20W7t41UjXz2S%2FwGrPZuN0%2FU8dJCnYyIW8Oy%2BKMNFtHCm8SZ%2BWTQy7adWZAA0DxDYfkVCnFm7rReJCRDJ3fCT%2FxcAS7wDfSrD%2FS8N2Af4Uwu%2F0fB6s3Nqd2vstPpbDVivcSRehJvhtHlMkLe7S%2FjH3SuuTRwQZgreBlEJLOu7PW6ql5iVrzq6WBrT8K2gdBcl%2FxGOg9zu4WNTZ1svhPtRR2hsmp7%2F%2FipSVxHXAHrcFzeTk6RHe9d1rlhBE%2FPZ%2Bga9oeeXV3WNpSMXyAp8qY3473R6QMRa%2FYIuJX7QC8FWnohJOQ9dsKfwnIevmJTOqaTAKK%2Bvdcu2YZBK%2FFPSQH4Oz8SyyjHyJdRllarrHtjC0NO6QXa5xMh3KvbOneLEP8gvVk%2BBXGxSRWmUHV%2FlrewVS63nuD5YwkDUHMzN0C4cbP5COQjyTKHoI2fUI57zZ3LKc2%2FD%2FdX1atH1MQHIu5hULSFADdIud%2Bf%2BX7YAuhLjjq7Ayrwc%2FdF1f4CJQnJy29ixrmLiR4Baefxkm3fCC4xT5lV7urlBE2w7Q4VS4m7NNoegFtsljb%2F%2Bt4dPPS55tRskc91XFlkycdaRFDJ5xiuGnCSs5tfDSpSnqhAhHIoqPmtmjhMaFG86B2bf4w1tHR0AY6sgHcFnc%2BGXu%2BTJgJcLqG%2BM8uoI%2Fb4WFQ0jvMOZE0vq%2FI0l8ddIOck5U%2BST1MZfWSQb2RxwT2p5L2D8uDzM4ETMEyqr2ne1vHV6aVoTgJ8wYlCqhvelOqyJmdaKg05SMgJ24wEEg%2BRzA9ogPjQ2KPLtosKMlYIXUY12zr79sdAvSnzBr7jep4mbJMPhND331orXt1Tuwo8gngEZOqEf4WXJRWarWhS8D4icscuaK0NOQKHIyu&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260525T155119Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIA4Z4ZKO5D3TRZBGEH%2F20260525%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=1e6aa0d6a84f7042a95d5e3fb03df692f2a42429cb823c6e8cece5ad1e28a44b",
    "expiresAt": "2026-06-01T15:51:19.340133189Z"
}
```

Presigned URL의 유효기간은 7일로 설정했습니다.

### Presigned URL 검증

IAM Role 방식으로 진행했기 때문에, 아래에 Presigned URL 접근 성공 스크린샷을 함께 첨부합니다.

<img width="2404" height="1269" alt="스크린샷 2026-05-26 오전 12 52 28(2)" src="https://github.com/user-attachments/assets/fc0adb43-f18a-409c-8c8b-c8d2fa3fd61d" />

## LV 4. Docker & CI/CD 파이프라인 구축

### Docker Hub 기반 자동 배포

GitHub Actions를 사용하여 `main` 브랜치에 코드가 Push되면 다음 과정이 자동으로 실행되도록 구성했습니다.

1. Gradle Build 및 Test 수행
2. Docker 이미지 빌드
3. Docker Hub에 이미지 Push
4. EC2에서 최신 이미지 Pull
5. 기존 컨테이너 교체 후 새 컨테이너 실행

### GitHub Actions 배포 성공 화면

<img width="610" height="174" alt="스크린샷 2026-05-25 오후 11 58 49" src="https://github.com/user-attachments/assets/738b3619-4585-4e5b-acb5-96c6457ef9b3" />

### EC2 Docker 컨테이너 실행 화면

<img width="1025" height="292" alt="스크린샷 2026-05-26 오전 12 02 08" src="https://github.com/user-attachments/assets/2066a0db-44b5-44de-879b-46a6d8d42655" />
