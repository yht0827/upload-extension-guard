# 파일 확장자 차단 시스템

파일 업로드 시 보안 위험이 있는 확장자를 관리하는 시스템

## 배포

| 환경 | URL |
|------|-----|
| 서비스 | https://upload-extension-guard.onrender.com |
| API 문서 | https://upload-extension-guard.onrender.com/swagger-ui.html |

> 무료 플랜으로 첫 접속 시 10~15초 지연될 수 있습니다.

## 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Java 21, Spring Boot 3.5, JPA |
| Frontend | Thymeleaf, Vanilla JS |
| Database | H2 (dev), PostgreSQL (prod) |
| API Docs | Swagger |
| Deploy | Render (Docker) |

## 주요 기능

- **고정 확장자**: bat, cmd, com, cpl, exe, scr, js 차단 토글
- **커스텀 확장자**: 사용자 정의 확장자 추가/삭제 (최대 200개)
- **입력값 정규화**: `.SH ` → `sh`
- **동시성 제어**: 비관적 락으로 200개 제한 보장

## API

| 기능 | Method | URI |
|------|--------|-----|
| 목록 조회 | GET | `/api/extensions` |
| 고정 상태 변경 | PATCH | `/api/extensions/fixed` |
| 커스텀 추가 | POST | `/api/extensions/custom` |
| 커스텀 삭제 | DELETE | `/api/extensions/custom/{id}` |

## 로컬 실행

```bash
./gradlew bootRun

# 접속
http://localhost:8080
http://localhost:8080/swagger-ui.html
http://localhost:8080/h2-console
```

## 문서

- [설계서](docs/SPEC.md) - 요구사항, 시퀀스 다이어그램, ERD
- [기술 결정](docs/DECISIONS.md) - 주요 기술 선택 이유
