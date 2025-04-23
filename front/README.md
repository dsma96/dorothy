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

### prerequisite
- mvn
- React, npm, vite
- JDK 17
- node 22.14
- docker
- MariaDB

### build
- npm run release 
- mvn clean install package
- docker build -t [IMAGE_TAG]:[VERSION] .


### run
- npm run dev
