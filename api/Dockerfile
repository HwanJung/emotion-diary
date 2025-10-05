# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 캐시 최적화: 먼저 그래들 래퍼/설정만 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle* settings.gradle* ./
RUN chmod +x gradlew

# 의존성만 먼저 다운로드(캐시)
RUN ./gradlew --no-daemon dependencies || true

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# ---------- Run stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 산출물 복사
COPY --from=build /workspace/build/libs/*.jar app.jar

# 헬스체크/메트릭 노출 시 포트(문서화용)
EXPOSE 8080

# JVM 옵션은 환경변수로 주입 가능: JAVA_OPTS
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]
