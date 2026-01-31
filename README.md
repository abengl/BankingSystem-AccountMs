# Account Microservice - Banking System

A scalable microservice for managing bank accounts within a distributed banking system. Implements contract-first API design, inter-service transaction processing, and comprehensive account lifecycle management.

## 🎯 Overview

This microservice handles all account-related operations including account creation, balance management, and transfer execution. It serves both client-facing requests and internal microservice communication, implementing complex business rules for account operations and financial transactions.

## 🏗️ Architecture & Design

### Key Features
- **Contract-First API Design**: OpenAPI 3.0 specifications ensure API consistency and type safety
- **API Architecture**: Public REST API for clients and internal API for microservice orchestration
- **Transactional Integrity**: JPA transactions for data consistency during financial operations
- **Domain-Driven Design**: Clear boundaries between account domain and external services
- **Service Integration**: Validates customers before account creation, executes transfers for transaction service

### Technology Stack
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: MySQL with JPA/Hibernate
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **Testing**: JUnit 5, Mockito
- **Code Quality**: JaCoCo (70% coverage), Checkstyle
- **Build Tool**: Maven

## 📋 API Endpoints

### Client-Facing API (`/api/v1/accounts`)
- `GET /` - Retrieve all active accounts
- `GET /{accountId}` - Retrieve account by ID
- `POST /` - Create new account (validates customer first)
- `PATCH /activate/{accountId}` - Activate account
- `PATCH /deactivate/{accountId}` - Deactivate account (requires zero balance)
- `GET /customer/{customerId}` - Get all accounts for a customer

### Internal API (`/api/v1/internal/accounts`)
- `PATCH /execute-transfer` - Execute balance transfer between accounts
- `GET /is-active/customer/{customerId}` - Check if customer has active accounts

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+

### Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/abengl/BankingSystem-AccountMs.git
   cd account-ms
   ```

2. **Configure database**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/account_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # Customer service URL for validation
   customer.ms.url=http://localhost:8085/api/v1/internal/customers/validate-customer
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The service will start on `http://localhost:8086`

### API Documentation

Access interactive API documentation at:
- **Swagger UI**: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
- **OpenAPI Spec**: [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs)

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Generate Coverage Report
```bash
mvn clean test jacoco:report
```
View report at `target/site/jacoco/index.html`

### Code Quality Check
```bash
mvn checkstyle:check
```

## 📂 Project Structure

```
account-ms/
├── src/main/java/com/alessandragodoy/accountms/
│   ├── api/                    # Generated API interfaces
│   │   ├── AccountApi.java
│   │   └── internal/
│   │       └── InternalAccountApi.java
│   ├── controller/             # API implementations
│   │   ├── AccountController.java
│   │   └── InternalAccountController.java
│   ├── dto/                   # Data Transfer Objects
│   ├── service/               # Business logic
│   │   ├── IAccountService.java
│   │   ├── IInternalAccountService.java
│   │   └── impl/
│   ├── model/                 # JPA entities
│   ├── repository/           # Data access layer
│   ├── adapter/              # External service clients
│   ├── exception/            # Custom exceptions
│   └── utility/              # Helper classes
├── src/main/resources/
│   ├── openapi/              # API contracts
│   │   ├── account-api.yml
│   │   └── internal-account-api.yml
│   └── application.properties
└── pom.xml
```

## 🔗 Integration

### Microservice Communication

This service integrates with:
- **Customer Microservice**: Validates customer existence and status before account creation
- **Transaction Microservice**: Receives transfer execution requests and updates account balances

## 📊 Code Quality Metrics

- **Test Coverage**: Minimum 70% line and instruction coverage
- **Code Style**: Google Java Style Guide compliance
- **Excluded from Coverage**: Configuration, DTOs, generated code, exceptions, utilities

## 🎓 Technical Highlights

- **Contract-First Development**: OpenAPI specs drive API design and implementation
- **Transactional Consistency**: JPA @Transactional ensures data integrity during transfers
- **Service Integration**: RESTful communication with Customer microservice
- **Domain Logic**: Complex business rules for account lifecycle management
- **Type-Safe APIs**: Generated interfaces ensure compile-time contract validation
- **Clean Architecture**: Separation of concerns across controller, service, repository layers
- **Comprehensive Testing**: Unit tests with Mockito, integration scenarios

## 📫 Contact

**Alessandra Godoy**
- Email: api@alessandragodoy.com
