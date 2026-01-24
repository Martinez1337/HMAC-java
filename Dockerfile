FROM amazoncorretto:21-alpine

WORKDIR /app

COPY lib/ ./lib/
COPY resources/ ./resources/
COPY src/ ./src/
COPY config.json ./config.json
COPY secret-manager-cli.sh ./secret-manager-cli.sh

RUN mkdir out && \
    find src -name "*.java" > sources.txt && \
    javac -cp "lib/*:src" -d out @sources.txt && \
    rm sources.txt

EXPOSE 8080

CMD ["java", "-cp", "lib/*:out", "-Dlogback.configurationFile=resources/logback.xml", "io.github.martinez1337.hmac.api.ServerHMAC"]
