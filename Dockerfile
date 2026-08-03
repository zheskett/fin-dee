FROM gradle:9.1.0-jdk21-jammy AS builder
WORKDIR /app

RUN apt update && apt install -y libatomic1

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/

COPY core/build.gradle.kts core/
COPY server/build.gradle.kts server/
COPY app/webApp/build.gradle.kts app/webApp/
COPY app/shared/build.gradle.kts app/shared/

RUN gradle dependencies --no-daemon

COPY . .
RUN gradle :server:buildFatJar :app:webApp:wasmJsBrowserDistribution --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/server/build/libs/*-all.jar /app/server.jar
COPY --from=builder /app/app/webApp/build/dist/wasmJs/productionExecutable /app/static

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/server.jar"]
