# Member Card

팀원 정보를 저장하고 조회할 수 있는 Spring Boot 기반 API 서버입니다.  
AWS EC2 환경에 배포하여 외부에서 Actuator Health Check를 통해 서버 상태를 확인할 수 있도록 구성했습니다.

---

## LV 0. AWS Budget 설정

클라우드 실습 중 비용 초과를 방지하기 위해 AWS Budgets를 설정했습니다.

### 설정 내용

- 월 예산: `$100`
- 알림 조건: 예산의 `80%` 도달 시 이메일 알림 발송

### AWS Budgets 설정 화면

![img.png](img.png)

---

## LV 1. 네트워크 구축 및 핵심 기능 배포

### 배포 및 검증
Spring Boot 애플리케이션을 EC2에 배포하고 실행했습니다.

### EC2 Public IP : 3.35.18.204

---

## LV 2. DB 분리 및 보안 연결

### RDS 구축

로컬 테스트 이후 실제 배포 환경에서는 AWS RDS MySQL을 사용하도록 구성했습니다.  
Spring Boot 애플리케이션은 EC2에서 실행되며, RDS에 연결하여 회원 정보를 저장하고 조회합니다.

### Parameter Store 설정

DB 접속 정보는 코드에 직접 작성하지 않고 AWS Systems Manager Parameter Store에 저장했습니다.

저장한 파라미터는 다음과 같습니다.

- `/config/member-card/url`
- `/config/member-card/username`
- `/config/member-card/password`
- `/config/member-card/team-name`

Spring Boot 실행 시 `prod` profile에서 Parameter Store 값을 주입받아 RDS에 연결하도록 설정했습니다.

### Actuator Info 검증

Parameter Store에 저장한 `team-name` 값을 `/actuator/info` 엔드포인트에서 확인할 수 있도록 설정했습니다.

### Actuator Info 엔드포인트 URL: http://3.35.18.204:8080/actuator/info

응답 예시:

```json
{
  "team-name": "ec2"
}
```

RDS 보안 그룹 설정
RDS 보안 그룹의 인바운드 규칙에는 직접 IP 주소를 등록하지 않고, EC2의 보안 그룹 ID를 Source로 등록했습니다.

이를 통해 EC2에서만 RDS에 접근할 수 있도록 보안 그룹 체이닝을 구성했습니다.

아래 스크린샷에서 RDS 보안 그룹 인바운드 규칙의 Source가 0.0.0.0/0이 아닌 EC2 보안 그룹 ID로 설정되어 있음을 확인할 수 있습니다.
<img width="1672" height="491" alt="스크린샷 2026-05-21 오후 6 34 27(2)" src="https://github.com/user-attachments/assets/aef6415f-fe6d-4be3-9ef2-ed874e480865" />
