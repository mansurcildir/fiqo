FROM eclipse-temurin:25-alpine

RUN addgroup fiqo && adduser --disabled-password --no-create-home --ingroup fiqo fiqo
USER fiqo

LABEL org.opencontainers.image.description="Fiqo Backend"
ADD target/fiqo-backend.jar fiqo-backend.jar
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/fiqo-backend.jar", "1>/dev/stdout", "2>/dev/stderr"]
