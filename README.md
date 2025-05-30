# Device Management API

A SpringBoot-based RESTful API that provides CRUD functionality for managing devices. 
This API uses MongoDB for data persistence and is integrated with Springdoc OpenAPI for automatic API documentation.

## Features

- Add, update, retrieve, and delete devices.
- SHA-256 encrypted unique device id generator
- MongoDB-backed persistence using Spring Data MongoDB.
- Layered architecture with service, repository, aggregator and controller layers.
- Centralized exception handling via `@ControllerAdvice`.
- API documentation with Swagger UI using Springdoc OpenAPI.
- Built with Spring Boot 3.1.8 (for compatibility with Springdoc 2.1.0).

---

## Tech Stack
    Framework: Spring Boot 3.1.8
    Language: Java21+
    Database: MongoDB
    Build Tool: Maven
    Swagger: Springdoc OpenAPI 2.1.0 
    Container: Docker

## Project Structure

    src/
        └── main/
        ├── java/
        │ └── com.example.devicecrud/
             ├── controller/
             ├── aggregator/
             ├── service/
             ├── repository/
             ├── model/
             ├── exception/
             ├── constants/
        │ └── DeviceCrudApplication.java
        └── resources/
        ├── application.yml
        └── static/

##  Getting Started

### Prerequisites

    - Java 21+
    - Maven 3.6+
    - MongoDB (running locally or on a server)
    - Docker installed and running on your system (only needed if you want to run the app locally through docker)
    - Access to the internet for downloading base images



### Clone the repo
    bash
    git clone https://github.com/your-username/device-resource-api.git
    cd device-resource-api

### Build the application locally without docker

    mvn clean install

### Run the application locally without docker

    mvn spring-boot:run

The API will be available at: http://localhost:8080

### Run the application locally through docker

    docker build -t devicecrud:latest (Build)
    docker run -d --name devicecrud -p 8080:8080 devicecrud:latest

Navigate to http://localhost:8080/swagger-ui/index.html to test the app

NOTE: The Mongo ATLAS URI will work from any system since I have added 0.0.0.0/0 in Atlas Network Access.

### Access Application Online (Recommended)

Route to: https://devicecrud.onrender.com/swagger-ui/index.html#/device-crud-controller to access the API(s) online
and connect to mongodb+srv://sridatree70:8K4h0qxDJQvpXp24@testcluster.bhqmzqa.mongodb.net/device-crud?retryWrites=true&w=majority to check the database.
(App takes about 1-2 minutes to load in Render since it is a free instance)

### API Endpoints
    POST /private/v1/device/create - Create a new device

    PUT /private/v1/device/update/{id} - Update a device based on the update request and the device id provided

    PUT /private/v1/device/updateBrand/{id}/{newBrand} - Update brand of a device based on the given id and the new brand
    
    GET /private/v1/device/fetch/{id} - Get device by device id

    GET /private/v1/device/fetch - Get all devices, devices of a particular brand or state or both
    
    POST /api/devices - Add a new device
    
    PUT /api/devices/{id} - Update device
    
    DELETE /private/v1/device/{id} - Delete device based on its id provided


### API Documentation (Swagger)
    To test in local, one of the two things needs to be done.
    Once the app is running, navigate to the below link to test the endpoints:

🔗 http://localhost:8080/swagger-ui/index.html


