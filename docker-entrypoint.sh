#!/bin/bash
exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=cloud -jar /app.jar
