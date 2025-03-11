#!/bin/bash
export DB_PWD=!DORmdssws00
export DB_USR=dorothy
export DB_URL=jdbc:mariadb://192.168.50.130:3306/dorothy
export JWT_SECRET=DorothyDorothyDorothyDorothyDorothyDorothyDorothy

exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=cloud -jar /app.jar
