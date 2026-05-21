# Member Card

팀원 정보를 저장하고 조회할 수 있는 Spring Boot 기반 API 서버입니다.  
AWS EC2, RDS, Parameter Store, S3를 사용하여 배포 환경을 구성했습니다.

---

## LV 0. AWS Budget 설정

클라우드 실습 중 비용 초과를 방지하기 위해 AWS Budgets를 설정했습니다.

### 설정 내용

- 월 예산: `$100`
- 알림 조건: 예산의 `80%` 도달 시 이메일 알림 발송

### AWS Budgets 설정 화면

![AWS Budget 설정 화면](img.png)

---

## LV 1. 네트워크 구축 및 핵심 기능 배포

Spring Boot 애플리케이션을 EC2에 배포하고 실행했습니다.

### EC2 Public IP

```text
3.35.18.204
```

---

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
  "presignedUrl": "https://...",
  "expiresAt": "2026-05-28T14:40:11.507045424Z"
}
```

Presigned URL의 유효기간은 7일로 설정했습니다.

### Presigned URL 검증

제출 직전에 `GET /api/members/{id}/profile-image`를 호출하여 새 Presigned URL을 발급받았습니다.

Presigned URL:

```text
제출 직전에 발급받은 Presigned URL을 여기에 작성
```

만료 시간:

```text
GET /api/members/{id}/profile-image 응답의 expiresAt 값을 여기에 작성
```

IAM Role 방식으로 진행했기 때문에, 아래에 Presigned URL 접근 성공 스크린샷을 함께 첨부합니다.

<!-- Presigned URL로 이미지 접근에 성공한 브라우저 화면 스크린샷을 여기에 첨부하세요. -->
