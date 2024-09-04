# docker build -t order .
# docker-compose down
# docker-compose up -d --build

FROM eclipse-temurin:21-jre-alpine
COPY build/libs/order-V2.jar /app/order-V2.jar
ENTRYPOINT [ "java", "-jar", "order-V2.jar" ]
EXPOSE 8080
RUN apk --update --no-cache add curl
HEALTHCHECK --interval=1m --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
LABEL version="0.1" \
    description="Order microservice using Postgres\
    including Docker containers and health check test"