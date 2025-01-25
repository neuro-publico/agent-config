FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/* /app/

EXPOSE 8080
CMD ["sh",  "-c" ,"java -jar -Dnetworkaddress.cache.ttl=30 -Dnetworkaddress.cache.negative.ttl=10 -XX:MaxGCPauseMillis=100 -XX:+UseG1GC /app/*.jar" ]
