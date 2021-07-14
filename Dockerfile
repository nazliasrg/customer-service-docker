FROM openjdk:8-jdk-alpine
COPY target/customer-service.jar customer-service.jar
ENTRYPOINT ["java", "-jar", "customer-service.jar"]