FROM eclipse-temurin:21 AS app-build

WORKDIR application

COPY target/*.jar application.jar

RUN java -Djarmode=layertools -jar application.jar extract


FROM eclipse-temurin:21

# Install necessary packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8080
ENV PORT=8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1

WORKDIR application

COPY --from=app-build application/spring-boot-loader/ ./
COPY --from=app-build application/dependencies/ ./
COPY --from=app-build application/snapshot-dependencies/ ./
COPY --from=app-build application/application/ ./

ENTRYPOINT ["java", \
            "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
            "--add-opens", "java.base/java.util=ALL-UNNAMED", \
            "--add-opens", "java.base/sun.net=ALL-UNNAMED", \
            "org.springframework.boot.loader.launch.JarLauncher"]
