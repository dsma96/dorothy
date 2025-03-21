# Start with a base image containing Java runtime
FROM amazoncorretto:17.0.7-alpine
ENV TZ="America/Toronto"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# Add a volume pointing to /tmp
VOLUME /tmp
# Make port 8080 available to the world outside this container
EXPOSE 8080 8443

# The application's jar file
ARG JAR_FILE=target/dorothy-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar
COPY ./docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN [ "chmod","+x", "/usr/local/bin/docker-entrypoint.sh"]
# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]