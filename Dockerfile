FROM node:22-alpine AS ui
WORKDIR /ui
COPY frontend/package*.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
RUN npm run build
FROM maven:3.9.11-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn -B dependency:go-offline
COPY backend/src ./src
COPY --from=ui /ui/dist/retailflow/browser/ ./src/main/resources/static/
RUN mvn -B verify
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S retail && adduser -S retail -G retail
COPY --from=backend /app/target/retailflow-1.0.0.jar app.jar
USER retail
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0"
ENTRYPOINT ["java","-jar","app.jar"]
