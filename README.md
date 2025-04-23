# Hair Shop Reservation system

Lightweight hair shop appointment system.

## Features
- create / modify appointment
- Image files uploading for reservation
- Configurable off day
- Daily/Hourly notification for appointment 
- Cellphone number verification using SMS
- Customizable User Role 
- Mileage deposit & redeem
- 
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
- DB_URL : maria db JDBC connection URI g
- DB_PWD : USER ID for maria DB
- JWT_SECRET : JWT token encrypt secret key
- TWILIO_SID : Twilio account id
- TWILIO_AUTH : Twilio auth token
- TWILIO_FROM : Twilio sender number
- VOLUME_PATH : Volume path for image files

#### TODO
- user feedback board
- support multiple instance
- support multiple designer
- support multiple branches / shops
- support multiple language
