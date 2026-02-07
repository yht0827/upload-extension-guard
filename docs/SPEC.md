# 파일 확장자 차단 시스템 설계서

> 파일 업로드 시 보안 위험이 있는 확장자를 차단하는 관리 시스템

---

# 1. 요구사항

## 1.1 개요

파일 첨부 시 보안에 문제가 될 수 있는 확장자를 차단하는 기능입니다.
`exe`, `sh` 등의 실행파일이 서버에 업로드되어 실행될 수 있는 위험을 방지합니다.

## 1.2 용어 정의

| 용어 | 영문 | 설명 |
|------|------|------|
| 고정 확장자 | FixedExtension | 시스템 기본 제공 차단 대상 (bat, cmd 등) |
| 커스텀 확장자 | CustomExtension | 관리자가 직접 추가한 차단 대상 |
| 차단 상태 | Blocked | 해당 확장자의 파일 업로드 차단됨 |
| 정규화 | Normalize | 입력값을 표준 형식으로 변환 |

## 1.3 API 요약

| 기능 | Method | URI |
|------|--------|-----|
| 전체 목록 조회 | GET | `/api/extensions` |
| 고정 확장자 상태 변경 | PATCH | `/api/extensions/fixed` |
| 커스텀 확장자 추가 | POST | `/api/extensions/custom` |
| 커스텀 확장자 삭제 | DELETE | `/api/extensions/custom/{id}` |

## 1.4 기능 요구사항

### 고정 확장자

| 항목 | 설명 |
|------|------|
| 대상 | `bat`, `cmd`, `com`, `cpl`, `exe`, `scr`, `js` |
| 기본 상태 | 모두 미차단 (unchecked) |
| 저장 | 체크 시 DB 저장 |

### 커스텀 확장자

| 항목 | 설명 |
|------|------|
| 최대 길이 | 20자 |
| 최대 개수 | 200개 |
| 추가 | 버튼 클릭 시 DB 저장 |
| 삭제 | X 버튼 클릭 시 DB 삭제 |

### 입력값 정규화

| 입력 | 결과 | 처리 |
|------|------|------|
| `.sh` | `sh` | 점(.) 제거 |
| `SH` | `sh` | 소문자 변환 |
| ` sh ` | `sh` | 공백 제거 |

### 검증 규칙

| 항목 | 규칙 | 에러 코드 |
|------|------|----------|
| 허용 문자 | `^[a-z0-9]+$` | 400 |
| 최대 길이 | 20자 | 400 |
| 중복 체크 | 고정/커스텀 모두 | 409 |
| 최대 개수 | 200개 | 422 |

---

# 2. 시퀀스 다이어그램

## 2.1 전체 확장자 목록 조회

```mermaid
sequenceDiagram
    autonumber
    actor User as 사용자
    participant Server as API 서버
    participant DB as Database

    User->>Server: GET /api/extensions
    Server->>DB: 고정 확장자 조회
    Server->>DB: 커스텀 확장자 조회
    Server-->>User: 200 OK (fixed + custom)
```

## 2.2 고정 확장자 상태 변경

```mermaid
sequenceDiagram
    autonumber
    actor User as 사용자
    participant Server as API 서버
    participant DB as Database

    User->>Server: PATCH /api/extensions/fixed<br/>{extension: "exe", blocked: true}
    Server->>DB: 확장자 조회
    alt 미존재
        Server-->>User: 404 Not Found
    else 존재
        Server->>DB: blocked 업데이트
        Server-->>User: 200 OK
    end
```

## 2.3 커스텀 확장자 추가

```mermaid
sequenceDiagram
    autonumber
    actor User as 사용자
    participant Server as API 서버
    participant DB as Database

    User->>Server: POST /api/extensions/custom<br/>{extension: "sh"}
    Server->>Server: 정규화 + 검증

    alt 형식 오류
        Server-->>User: 400 Bad Request
    else 중복
        Server-->>User: 409 Conflict
    else 200개 초과
        Server-->>User: 422 Unprocessable Entity
    else 성공
        Server->>DB: INSERT
        Server-->>User: 201 Created
    end
```

## 2.4 커스텀 확장자 삭제

```mermaid
sequenceDiagram
    autonumber
    actor User as 사용자
    participant Server as API 서버
    participant DB as Database

    User->>Server: DELETE /api/extensions/custom/1
    Server->>DB: 확장자 조회 (id=1)
    alt 미존재
        Server-->>User: 404 Not Found
    else 존재
        Server->>DB: DELETE
        Server-->>User: 204 No Content
    end
```

---

# 3. ERD

```mermaid
erDiagram
    fixed_extension {
        varchar extension PK "확장자 (최대 20자)"
        boolean blocked "차단 여부"
        timestamp created_at "생성일시"
        timestamp updated_at "수정일시"
    }

    custom_extension {
        bigint id PK "식별자"
        varchar extension UK "확장자 (UNIQUE)"
        timestamp created_at "생성일시"
    }

    extension_lock {
        varchar id PK "LOCK"
    }
```

## 3.1 테이블 상세

### fixed_extension

| 컬럼 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| extension | VARCHAR(20) | PK | 확장자명 |
| blocked | BOOLEAN | NOT NULL, DEFAULT FALSE | 차단 여부 |
| created_at | TIMESTAMP | | 생성일시 |
| updated_at | TIMESTAMP | | 수정일시 |

**초기 데이터:** bat, cmd, com, cpl, exe, scr, js (모두 blocked=false)

### custom_extension

| 컬럼 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 식별자 |
| extension | VARCHAR(20) | UNIQUE, NOT NULL | 확장자명 |
| created_at | TIMESTAMP | | 생성일시 |
| updated_at | TIMESTAMP | | 수정일시 |

### extension_lock

| 컬럼 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| id | VARCHAR | PK | 락 식별자 ('LOCK') |

> 동시성 제어를 위한 비관적 락 테이블

---

# 4. 프로젝트 구조

```
src/main/java/com/example/uploadextensionguard/
├── config/           # JPA Auditing, DataInitializer
├── controller/       # ExtensionController, PageController
├── service/          # ExtensionService
├── repository/       # JPA Repository
├── entity/           # FixedExtension, CustomExtension, ExtensionLock
├── dto/              # Request/Response DTO
└── exception/        # GlobalExceptionHandler, 커스텀 예외

src/main/resources/
├── templates/        # Thymeleaf (index.html)
├── static/           # CSS, JS
└── application.yml   # 환경 설정 (local/prod)
```

---

# 5. 테스트

## 5.1 테스트 범위

| 계층 | 테스트 클래스 | 테스트 방식 |
|------|--------------|------------|
| Entity | CustomExtensionTest | 단위 테스트 |
| Repository | CustomExtensionRepositoryTest | @DataJpaTest |
| Service | ExtensionServiceTest | 단위 테스트 (Mock) |
| Service | ExtensionServiceConcurrencyTest | 동시성 테스트 |
| Controller | ExtensionControllerTest | @WebMvcTest |
| Exception | GlobalExceptionHandlerTest | 단위 테스트 |

## 5.2 주요 테스트 케이스

### Entity (CustomExtension)

| 테스트 | 설명 |
|--------|------|
| 정상 생성 | 영문 소문자, 숫자 포함 확장자 |
| 정규화 | 대문자→소문자, 공백 제거, 점 제거 |
| 검증 실패 | 빈 값, 20자 초과, 특수문자 |
| 경계값 | 1자, 20자 확장자 |

### Service (ExtensionService)

| 테스트 | 설명 |
|--------|------|
| 커스텀 추가 | 정상 추가, 위험 확장자 경고 |
| 중복 검증 | 커스텀 중복, 고정 확장자 충돌 |
| 형식 검증 | 특수문자, 공백 포함 시 예외 |
| 개수 제한 | 200개 초과 시 예외 |
| 삭제 | 정상 삭제, 미존재 시 예외 |
| 고정 상태 변경 | 차단/해제, 미존재 시 예외 |

### Controller (ExtensionController)

| 테스트 | 설명 |
|--------|------|
| GET /api/extensions | 전체 조회 성공 |
| PATCH /api/extensions/fixed | 상태 변경, 404, 400 |
| POST /api/extensions/custom | 추가 201, 중복 409, 초과 400 |
| DELETE /api/extensions/custom/{id} | 삭제 204, 404 |

### 동시성 (Concurrency)

| 테스트 | 설명 |
|--------|------|
| 중복 추가 | 동시에 같은 확장자 추가 시 하나만 성공 |
| 200개 제한 | 195개 + 동시 10개 요청 → 정확히 200개만 저장 |

---

# 6. 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Java 21, Spring Boot 3.5, JPA |
| Frontend | Thymeleaf (SSR), Vanilla JS |
| Database | H2 (개발), PostgreSQL (배포) |
| API Docs | Swagger (springdoc-openapi) |
| Build | Gradle |
| Deploy | Render (Docker) |
