# Hair Shop Reservation system

Lightweight hair shop appointment system.

## Features
- create / modify appointment
- Configurable off day
- Daily/Hourly notification for appointment 
- Cellphone number verification using SMS
- Customizable User Role 

### prerequisite
- mvn
- React, npm, vite
- JDK 17
- node 22.14
- docker
- MariaDB

### build
- mvn clean install package
- docker build -t [IMAGE_TAG]:[VERSION] .

### Environment value
- DB_URL : maria db JDBC connection URI 
- DB_PWD : USER ID for maria DB
- JWT_SECRET : JWT token encrypt secret key
- TWILIO_SID : Twilio account id
- TWILIO_AUTH : Twilio auth token
- TWILIO_FROM : twilio sender number

#### TODO
- photo uploading for appointment
- support multiple instance
- support multiple designer