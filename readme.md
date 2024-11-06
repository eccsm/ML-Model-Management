# ML Model Management

This project is an ML Model Management service built with Java and Spring Boot. It allows users to manage machine learning models, including creating models, uploading training data, and initiating training processes. The project also includes basic user authentication and authorization mechanisms.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Key Packages and Classes](#key-packages-and-classes)
- [Configuration](#configuration)
- [Building and Running](#building-and-running)
- [Testing](#testing)
- [Technologies Used](#technologies-used)
- [License](#license)
- [Contact](#contact)

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven** for dependency management and build
- **Lombok Plugin** for your IDE (e.g., IntelliJ, Eclipse) to avoid compilation issues with Lombok annotations

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/eccsm/ml-model-management.git
   cd ml-model-management

2. **Configure Database:** Update properties with your database configuration.

    ```bash
    src/main/resources/application.properties 

3. **Install dependencies:**

    ```bash
    mvn install

### Project Structure

    src
    ├── main
    │   ├── java/net/casim/ml/mm
    │   │   ├── config               # Security and JWT configuration
    │   │   ├── controller           # REST API controllers
    │   │   ├── data                 # Entities and request classes
    │   │   │   └── request          # DTOs for handling requests
    │   │   ├── exception            # Custom exceptions
    │   │   ├── repository           # JPA repositories
    │   │   ├── service              # Business logic services
    │   │   └── utils                # Utility classes (e.g., JSON conversion)
    │   ├── resources                # Static resources, templates, and properties
    └── test
        └── java/net/casim/ml/mm/service # Unit tests for services

### Key Packages and Classes
**Config**

_JwtAuthenticationFilter:_ 

JWT authentication filter to validate tokens.

_SecurityConfig:_ 

Configures application security, including CORS, CSRF, and endpoint access restrictions.

**Controller**

AuthController: Manages user authentication endpoints (e.g., login)

ModelController: Handles operations related to ML models, such as creating a model, uploading training data, and starting training.

**Data**

_LLMModel:_ 

Represents a machine learning model entity.

_ModelLayer:_ 

Enum defining model layers (e.g., TEXT_CLASSIFIER, VISUAL_CLASSIFIER).

_TrainingData:_ 

Represents training data associated with a model.

_User:_ 

Represents application users.

_UserRole:_ 

Enum defining user roles (e.g., USER, ADMIN).

**Exception**

_ResourceNotFoundException:_ 

Custom exception thrown when a requested resource is not found.

**Repository**

_ModelRepository:_ 

Repository for accessing LLMModel data.

_TrainingDataRepository:_ 

Repository for accessing TrainingData data.

_UserRepository:_ 

Repository for accessing User data.

**Service**

_CustomUserDetailsService:_ 

Loads user-specific data used by Spring Security.

_FileUploadService:_ 

Handles file uploads for training data.

_JwtService:_ 

Service for generating and validating JWT tokens.

_ModelService:_ 

Manages ML model-related operations, including training.

_UserService:_ 

Manages user registration and authentication logic.

**Utils**

MapToJsonConverter: Utility for converting maps to JSON format, used for structured logging.

### Configuration

Configuration is handled via application.properties in the below directory.
```
src/main/resources
```
Update this file with your database connection details and other configuration settings.
Example configuration:

```properties
# Spring Boot application properties
spring.application.name=ML-Model-Management
spring.datasource.url={your URL}
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path={your URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.settings.web-allow-others=true

# File upload configurations
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
file.upload-dir={your directory}

# JWT security configurations
security.jwt.secret-key={your secret}
# 1 hour in milliseconds
security.jwt.expiration-time=3600000

```

### Building and Running
**Build the Application**

To build the application, use Maven:

```bash
mvn clean install
```

**Run the Application**
To run the application locally, execute:

```bash
mvn spring-boot:run
```
The server will start at http://localhost:8080.

### API Definitions

You can reach API endpoints via 
```plainText
/swagger-ui/index.html
```
with exploring 
```plainText
/v3/api-docs
```
### Testing
**Running Unit Tests**

Unit tests are located in the src/test/java/net/casim/ml/mm/service directory. To run the tests, use:

```bash
mvn test
```

**Test Classes**

_ModelServiceTest:_ 

Tests for ModelService, including model creation, retrieval, and training operations.

_UserServiceTest:_ 

Tests for UserService, including user registration and role validation.

**Adding New Tests**

To add a new test, create a new class in 
```bash
src/test/java/net/casim/ml/mm/service
```
annotate it with **@Test**, and implement your test cases using JUnit and Mockito.

### Technologies Used
* Java 17
* Spring Boot for application framework
* Spring Security for authentication and authorization
* JWT for token-based security
* Spring Data JPA with H2 for in-memory database persistence
* Lombok for reducing boilerplate code
* JUnit and Mockito for unit testing

### License
This project is licensed under the MIT License. See the LICENSE file for more details.

### Contact
For more information, please contact **ekincan@casim.net**
