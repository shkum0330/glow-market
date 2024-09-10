FROM openjdk:17-alpine
# 환경 설정
WORKDIR /app

# Gradle wrapper 복사 및 캐시 이용을 위한 종속성 파일 복사
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
RUN ./gradlew build --no-daemon || return 0

# 소스 코드 복사 및 빌드 실행
COPY src/ src/
RUN ./gradlew build --no-daemon -x test

ARG JAR_FILE=build/libs/*.jar
VOLUME /tmp
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]