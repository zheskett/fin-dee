# Stage 1: Cache Gradle dependencies
FROM gradle:9.6.1-jdk21-alpine AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle dependencies --no-daemon

# Stage 2: Build Application
FROM gradle:9.6.1-jdk21-alpine AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM eclipse-temurin:21-alpine AS runtime
EXPOSE 6767
VOLUME ["/data"]
RUN mkdir /app
RUN mkdir /data
COPY --from=build /home/gradle/src/build/libs/*.jar /app/fin-dee.jar
ENTRYPOINT ["java","-jar","/app/fin-dee.jar"]

