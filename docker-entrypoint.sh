#!/bin/bash
export DB_USR=dorothy
exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=cloud -jar /app.jar
