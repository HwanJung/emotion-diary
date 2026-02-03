# Dockerfile

# 1. 베이스 이미지 선택 (파이썬 3.9의 가벼운 버전)
FROM python:3.9-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. (중요) 빌드 속도 향상을 위해 requirements.txt만 먼저 복사
COPY requirements.txt .

# 4. 라이브러리 설치
# --no-cache-dir은 불필요한 캐시를 남기지 않아 이미지 크기를 줄여줍니다.
RUN python -m pip install --no-cache-dir --upgrade pip \
 && python -m pip install --no-cache-dir \
    torch==2.2.2 torchvision==0.17.2 \
    --extra-index-url https://download.pytorch.org/whl/cpu \
 && python -m pip install --no-cache-dir -r requirements.txt

# 5. 프로젝트의 모든 파일(app/ 폴더 등)을 컨테이너 안으로 복사
COPY . .

# 6. 서버가 사용할 포트를 외부에 알림
EXPOSE 8000

# 7. 컨테이너가 시작될 때 실행할 최종 명령어
# (0.0.0.0은 컨테이너 외부에서 접속을 허용하기 위해 필수)
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
