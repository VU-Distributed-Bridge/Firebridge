FROM openjdk:11 AS builder
COPY /gradle/ /app/gradle/
COPY gradlew /app/
COPY build.gradle.kts /app/
COPY settings.gradle.kts /app/
COPY gradle.properties /app/
COPY /src/ /app/src/
WORKDIR /app/
RUN ./gradlew shadowJar

FROM openjdk:11
COPY --from=builder /app/build/libs/firebridge-1.0-SNAPSHOT-all.jar /Firebridge.jar
ENTRYPOINT ["java", "-jar", "/Firebridge.jar"]
