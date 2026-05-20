# Member Card

팀원 정보를 저장하고 조회할 수 있는 Spring Boot 기반 API 서버입니다.  
AWS EC2 환경에 배포하여 외부에서 Actuator Health Check를 통해 서버 상태를 확인할 수 있도록 구성했습니다.

## LV 0. AWS Budget 설정

클라우드 실습 중 비용 초과를 방지하기 위해 AWS Budgets를 설정했습니다.

### 설정 내용

- 월 예산: `$100`
- 알림 조건: 예산의 `80%` 도달 시 이메일 알림 발송

### AWS Budgets 설정 화면

![img.png](img.png)

## LV 1. 네트워크 구축 및 핵심 기능 배포

### 배포 및 검증
Spring Boot 애플리케이션을 EC2에 배포하고 실행했습니다.

### EC2 Public IP : 3.35.18.204
