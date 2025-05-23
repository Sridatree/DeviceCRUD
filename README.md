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

### Clone the repo
    bash
    git clone https://github.com/your-username/device-resource-api.git
    cd device-resource-api

### Build the application

    mvn clean install

### Run the application

    mvn spring-boot:run

The API will be available at: http://localhost:8080

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
    Once the app is running, navigate to the below link to test the endpoints:

🔗 http://localhost:8080/swagger-ui/index.html