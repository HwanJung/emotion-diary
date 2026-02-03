# AI Server 리팩토링 기록

## 개요
REST API 방식에서 RabbitMQ 메시지 기반 비동기 처리 방식으로 리팩토링

## 도입 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| pydantic-settings | >=2.0.0 | 환경변수 관리 |
| aio-pika | >=9.0.0 | RabbitMQ 비동기 클라이언트 |
| asyncpg | >=0.29.0 | PostgreSQL 비동기 드라이버 |

## 새로운 프로젝트 구조

```
ai-server/
├── app/
│   ├── __init__.py
│   ├── main.py                     # FastAPI 앱 + lifespan 관리
│   ├── config/
│   │   ├── __init__.py
│   │   └── settings.py             # pydantic-settings 환경변수
│   ├── core/
│   │   ├── __init__.py
│   │   ├── database.py             # asyncpg 연결 풀
│   │   └── rabbitmq.py             # aio-pika Consumer
│   ├── models/
│   │   ├── __init__.py
│   │   └── fusion_model.py         # PyTorch 모델 정의
│   ├── schemas/
│   │   ├── __init__.py
│   │   └── message.py              # Pydantic 스키마
│   ├── services/
│   │   ├── __init__.py
│   │   ├── emotion_analyzer.py     # 감정 분석 서비스
│   │   └── message_handler.py      # 메시지 처리 오케스트레이션
│   ├── repositories/
│   │   ├── __init__.py
│   │   ├── diary_repository.py     # diaries 테이블 조회
│   │   └── analysis_repository.py  # emotion_analysis 업데이트
│   └── api/
│       ├── __init__.py
│       └── routes.py               # REST API 엔드포인트
├── model_params/                    # 모델 가중치 파일
├── requirements.txt
├── Dockerfile
└── .env
```

## 메시지 처리 플로우

```
Spring API Server
    │
    ▼ (publish)
RabbitMQ (diary.analysis.queue)
    │
    ▼ (consume)
AI Server
    │
    ├─▶ DB 조회: SELECT content FROM diaries WHERE id = ?
    │
    ├─▶ 감정 분석: predict(content, imageUrl)
    │       ├─ 이미지 있음 → Fusion 모델
    │       └─ 이미지 없음/실패 → Text 모델만
    │
    ├─▶ DB 업데이트: UPDATE emotion_analysis SET emotion, color_code, status, analyzed_at
    │
    └─▶ ACK/NACK
```

## 핵심 변환 규칙

| 항목 | 변환 전 | 변환 후 |
|------|---------|---------|
| 감정 레이블 | `Joy`, `Sadness`, ... | `JOY`, `SADNESS`, ... (대문자) |
| 분석 상태 | `PENDING` | `DONE` 또는 `FAILED` |
| 컬럼명 | camelCase | snake_case (`color_code`, `analyzed_at`) |

## 에러 처리 전략

| 에러 유형 | Status | 메시지 처리 |
|-----------|--------|-------------|
| 일기 없음 | FAILED | ACK (재시도 불필요) |
| 분석 실패 | FAILED | ACK (재시도 불필요) |
| DB 연결 실패 | 변경없음 | NACK (재시도) |
| 이미지 다운로드 실패 | DONE | ACK (텍스트만 분석) |

## 환경변수 (Spring과 공유)

```env
# PostgreSQL
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=emotion_diary
POSTGRES_USER=postgres
POSTGRES_PASSWORD=

# RabbitMQ (Spring과 동일한 변수명)
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
RABBITMQ_QUEUE=diary.analysis.queue
```

## 기존 REST API (유지)

| Endpoint | Method | 설명 |
|----------|--------|------|
| `/` | GET | 서버 정보 |
| `/health` | GET | 헬스체크 |
| `/analyze-diary-fusion` | POST | 감정 분석 (직접 호출용) |

## 레거시 파일 (삭제 가능)

다음 파일/폴더들은 새 구조로 이전되어 더 이상 사용되지 않음:

| 경로 | 설명 | 대체 |
|------|------|------|
| `main.py` (루트) | 기존 단일 파일 서버 | `app/main.py` |
| `models/` | 기존 모델 정의 | `app/models/` |
| `model_classes/` | 학습용 모델 클래스 | 서버에서 미사용 (학습 코드용) |

### 삭제 명령어
```bash
# 레거시 파일 삭제 (선택사항)
rm main.py
rm -rf models/
rm -rf model_classes/  # 학습 코드가 필요 없다면
```

## 실행 방법

### 로컬 실행
```bash
# 의존성 설치
pip install -r requirements.txt

# 서버 실행
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### Docker 실행
```bash
docker build -t ai-server .
docker run -p 8000:8000 --env-file .env ai-server
```

## 버그 수정 이력

### datetime timezone 호환성 문제
- **증상**: `can't subtract offset-naive and offset-aware datetimes`
- **원인**: PostgreSQL `timestamp without time zone` 컬럼에 timezone-aware datetime 전달
- **해결**: `datetime.utcnow()` 사용 (timezone-naive)

## 참고사항

- RabbitMQ `guest` 계정은 localhost에서만 접속 가능 (Docker 환경에서는 별도 계정 필요)
- Spring의 큐 설정과 동일하게 `durable=True`로 설정됨
- 모델 가중치 파일은 git에 포함되지 않으므로 별도 배포 필요
