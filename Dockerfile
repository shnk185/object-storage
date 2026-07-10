FROM amazoncorretto:17
WORKDIR /app
COPY "target/object-storage-0.0.1-SNAPSHOT.jar" app.jar
EXPOSE 8189
ENTRYPOINT ["java", "-jar", "app.jar"]