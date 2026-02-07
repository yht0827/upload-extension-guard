# 기술 결정 기록 (ADR)

> 프로젝트에서 내린 주요 기술적 결정과 그 이유

---

## 1. 배포 플랫폼: Render

**선택지:** Render / Railway / Fly.io

**결정:** Render

**이유:**
- PostgreSQL 무료 90일 제공
- GitHub 연동 자동 배포
- Docker 기반 배포로 환경 일관성 보장

**트레이드오프:**
- 15분 무응답 시 슬립 → 첫 접속 10~15초 지연
- README에 지연 가능성 안내로 대응

---

## 2. 브랜치 전략: main + dev + UEG-*

**선택지:** main만 / main + dev / main + dev + feat/*

**결정:** main + dev + UEG-* (이슈 번호 기반)

**구조:**
```
main (배포)
 └── dev (개발 통합)
      ├── UEG-1 (Entity)
      ├── UEG-2 (Service)
      └── ...
```

---

## 3. 도메인 모델: 부분적 Rich Domain

**선택지:** Anemic / Rich / 부분 적용

**결정:** 부분 적용 (Entity 자가 검증)

**책임 분배:**
| 책임 | 위치 | 이유 |
|------|------|------|
| normalize | Entity | 도메인 지식 |
| validate | Entity | 불변식 보장 |
| checkDuplicate | Service | Repository 의존 |
| checkMaxLimit | Service | Repository 의존 |

---

## 4. Repository: DIP 미적용

**선택지:** Port/Adapter 패턴 / Spring Data JPA 직접 사용

**결정:** Spring Data JPA 직접 사용

**이유:**
- 현재 규모에서 추상화는 오버엔지니어링
- Spring Data JPA가 이미 인터페이스 기반
- 테스트는 @DataJpaTest로 충분

---

## 5. 동시성 제어: Lock Table + 비관적 락

**문제:** 200개 제한의 동시성 이슈
```
Thread A: count=199 → 통과 → insert → 200개
Thread B: count=199 → 통과 → insert → 201개 (초과!)
```

**결정:** Lock Table + 비관적 락

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT e FROM ExtensionLock e WHERE e.id = 'LOCK'")
ExtensionLock acquireLock();
```

**왜 낙관적 락이 아닌가:**
- `@Version`은 기존 엔티티 수정 시 동작
- COUNT 쿼리 결과에는 버전이 없음
- "COUNT 후 INSERT" 패턴에는 비관적 락이 적합

**대안 비교:**
| 방식 | 장점 | 단점 |
|------|------|------|
| SERIALIZABLE | 간단 | H2 미지원, 성능 저하 |
| synchronized | 간단 | @Transactional과 충돌 |
| **Lock Table** | DB 레벨 보장 | 테이블 추가 필요 |

---

## 6. 뷰 렌더링: Thymeleaf (SSR)

**선택지:** Static HTML + Fetch (CSR) / Thymeleaf (SSR)

**결정:** Thymeleaf (SSR)

**비교:**
| 구분 | CSR | SSR |
|------|-----|-----|
| 첫 화면 | 빈 화면 → 로드 후 표시 | 즉시 표시 |
| 체감 속도 | 깜빡임 있음 | 빠름 |

**변경 후 흐름:**
1. 서버에서 DB 조회 + HTML 렌더링
2. 완성된 HTML 전송
3. 브라우저가 받자마자 표시

---

## 7. 입력값 정규화

**선택지:** 클라이언트에서 처리 / 서버에서 처리 / 양쪽 모두

**결정:** 서버에서 처리 (Entity 생성 시점)

**이유:**
- 클라이언트 검증은 우회 가능
- 서버에서 단일 정규화 로직으로 일관성 보장
- `.SH ` → `sh` (점 제거, 소문자 변환, trim)

---

## 8. 중복 체크 범위

**선택지:** 커스텀만 체크 / 고정 + 커스텀 모두 체크

**결정:** 고정 + 커스텀 모두 체크

**이유:**
- 고정 확장자와 동일한 커스텀 등록 시 혼란 발생
- 예: `exe`가 고정에 있는데 커스텀에도 등록되면 관리 복잡
- 409 Conflict로 명확한 에러 응답

---

## 9. 형식 검증

**선택지:** 정규식 검증 / 블랙리스트 방식 / 검증 없음

**결정:** 정규식 검증 (`^[a-z0-9]+$`)

**이유:**
- 영문 소문자 + 숫자만 허용
- 특수문자, 공백 등 비정상 입력 차단
- 정규화 후 검증하므로 대문자는 이미 소문자로 변환됨

---

## 10. 에러 메시지

**선택지:** 영문 메시지 / 한글 메시지 / 에러 코드만

**결정:** 한글 메시지

**이유:**
- 사용자 친화적 UX
- 프론트엔드에서 별도 변환 불필요
- 예: "이미 등록된 확장자입니다", "최대 200개까지 등록 가능합니다"

---

## 11. Profile 분리

**선택지:** 파일 분리 (application-*.yml) / 단일 파일

**결정:** 단일 파일 (application.yml)

**이유:**
- 프로젝트 규모가 작아 분리 필요성 낮음
- `---` 구분자와 `on-profile`로 환경 분리 충분
- 한 파일에서 전체 설정 파악 가능
