# 📓 Emotion Diary (감정 일기장)

Emotion Diary는 사용자가 일기를 작성하면 텍스트 기반 감정 분석을 수행하고,  
결과를 저장·조회할 수 있는 **웹 기반 감정 기록 서비스**입니다.

본 프로젝트는 모노레포(monorepo) 형태로 구성되어 있으며,  
API 서버, AI 분석 서버, 프론트엔드, 리버스 프록시를 하나의 저장소에서 관리합니다.

---

## ✨ Features

- Kakao, Google 소셜 로그인
- 일기 CRUD
- 감정 분석 요청 및 결과 저장
- JWT 기반 사용자 인증
- API 서버 ↔ AI 서버 분리 구조
- Docker Compose 기반 로컬 / 배포 환경 통합

---

## 🧱 Architecture

<img src="docs/emotion-diary-architecture.png" width="800"/>



### Components

- **web/** : Frontend
- **api/** : Spring Boot API Server
- **ai-server/** : FastAPI Emotion Analysis Server
- **reverse-proxy/** : Caddy Reverse Proxy
- **docker-compose.yml** : Service orchestration

---

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Redis

### AI Server
- Python
- FastAPI

### Infra
- Frontend: Vercel
- Backend: AWS EC2 + Docker Compose
- GitHub Container Registry (GHCR)

---

## 📌 Deployment

- Frontend is deployed on Vercel
- Backend services (API / AI / DB / Redis / Proxy) run on AWS EC2 using Docker Compose
- Docker images are built and pushed to GitHub Container Registry (GHCR)


