FROM bellsoft/liberica-openjre-alpine:21

COPY build/libs/powerFind-1.0.0.jar app.jar

ENTRYPOINT ["java", "-Xms384m", "-Xmx384m", "-jar", "/app.jar"]
