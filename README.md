# CineLock – High-Concurrency Movie Ticket Booking System

CineLock is a backend-only movie ticket booking system built using Java and Spring Boot.

The main goal of the project is to prevent double-booking when multiple users try to reserve the same movie seat at the same time.

---

## Problem Statement

In movie ticket booking systems, multiple users may try to book the same seat simultaneously.

Without proper concurrency control, this can cause:

- Double booking
- Inconsistent booking data
- Payment conflicts
- Poor user experience

CineLock solves this problem using database locking, transactions, Redis-based temporary seat holds, and controlled booking states.

---

## Key Features

- Movie management
- Show management
- Seat creation and availability tracking
- Temporary seat hold mechanism
- Automatic seat hold expiration
- High-concurrency booking handling
- Pessimistic database locking
- Booking lifecycle management
- Payment lifecycle management
- Automatic seat release on payment failure
- Spring Security authentication
- JWT-based stateless authentication
- USER and ADMIN role-based authorization
- Redis-based temporary seat holds with TTL
- Bean Validation
- Global Exception Handling
- Swagger / OpenAPI documentation
- MySQL persistence using Spring Data JPA
- Dockerized Spring Boot application
- Multithreaded concurrency testing

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Redis
- MySQL
- Maven
- Docker
- Swagger / OpenAPI
- JUnit
- Postman

---

## Architecture

```text
Client / Postman / Swagger
          |
          v
Spring Security + JWT Filter
          |
          v
Controller Layer
          |
          v
Service Layer
          |
          v
Repository Layer
          |
          v
MySQL Database

Redis
  |
  v
Temporary Seat Holds
```

The project follows a layered architecture to separate responsibilities and improve maintainability.

---

## Booking Flow

```text
AVAILABLE
   |
   v
HELD
   |
   v
Booking PENDING
   |
   v
Payment PENDING
   |
   v
Payment SUCCESS
   |
   v
Booking CONFIRMED
   |
   v
Seat BOOKED
```

If payment fails:

```text
Payment FAILED
     |
     v
Booking CANCELLED
     |
     v
Seat AVAILABLE
```

---

## Concurrency Handling

The main technical problem solved by CineLock is preventing multiple users from booking the same seat at the same time.

The application uses pessimistic database locking.

```text
User A --------\
                > Same Seat
User B --------/
                    |
                    v
             Database Row Lock
                    |
                    v
           One transaction proceeds
```

The seat row is locked inside a database transaction before booking.

This prevents another transaction from modifying the same seat until the current transaction is completed.

---

## Redis Seat Hold

When a user selects a seat, CineLock temporarily places the seat in a `HELD` state.

A Redis key is created using the following format:

```text
seat:hold:{seatId}
```

Example:

```text
seat:hold:13
```

The Redis key stores the username associated with the hold.

Example:

```text
seat:hold:13 = ravi
```

The hold is stored with a TTL of 2 minutes.

If the user does not complete the booking process within the allowed time, the hold expires.

This helps prevent seats from being blocked indefinitely.

---

## Security

CineLock uses Spring Security and JWT-based authentication.

After successful login, the user receives a JWT token.

Protected APIs require the token in the Authorization header:

```text
Authorization: Bearer <JWT_TOKEN>
```

### USER Role

A USER can access:

- Seats
- Bookings
- Payments

### ADMIN Role

An ADMIN can additionally manage:

- Movies
- Shows

This provides role-based authorization for different types of users.

---

## API Endpoints

### Authentication

```text
POST /auth/register
POST /auth/login
```

### Movies

```text
GET  /movies
POST /movies
```

### Shows

```text
GET  /shows
POST /shows
```

### Seats

```text
GET  /seats
POST /seats
PUT  /seats/{id}/hold
PUT  /seats/{id}/confirm
```

### Bookings

```text
GET  /bookings
POST /bookings
```

### Payments

```text
GET  /payments
POST /payments
PUT  /payments/{id}/success
PUT  /payments/{id}/failure
```

---

## Validation

CineLock uses Jakarta Bean Validation to validate incoming API requests.

Examples:

- Movie title cannot be empty
- Movie language cannot be empty
- Movie genre cannot be empty
- Duration must be greater than zero
- Username cannot be empty
- Password cannot be empty
- Seat number cannot be empty
- Payment amount must be greater than zero

Example invalid request:

```json
{
  "title": "",
  "language": "",
  "genre": "Action",
  "durationMinutes": 0
}
```

Invalid data is rejected before being stored in the database.

---

## Global Exception Handling

CineLock uses centralized exception handling with:

```java
@RestControllerAdvice
```

This allows validation errors and business exceptions to be handled from one place.

Examples include:

- Seat not found
- Seat not available
- Seat is not on hold
- Booking not found
- Payment not found
- Username already exists
- Invalid request data

---

## Swagger API Documentation

Swagger UI is used to view and test the REST APIs.

When running locally:

```text
http://localhost:8080/swagger-ui/index.html
```

When running using the current Docker port mapping:

```text
http://localhost:8081/swagger-ui/index.html
```

---

## Running the Project Locally

### 1. Clone the Repository

```bash
git clone <your-repository-url>
```

Move into the project folder:

```bash
cd CineLock
```

### 2. Configure MySQL

Create the database:

```sql
CREATE DATABASE flashguard_db;
```

Configure MySQL credentials in your local environment or application configuration.

Do not commit passwords or secrets to GitHub.

### 3. Start Redis

Redis should be available on:

```text
localhost:6379
```

On Windows, a Redis-compatible server such as Memurai can be used.

### 4. Run Spring Boot

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

### 5. Build the Application

```bash
.\mvnw.cmd clean package -DskipTests
```

The generated JAR file will be available inside the `target` directory.

---

## Docker

The Spring Boot application can also run inside a Docker container.

### Build Docker Image

```bash
docker build -t cinelock-app .
```

### Run Docker Container

```bash
docker run --name cinelock-container -p 8081:8080 cinelock-app
```

The application runs internally on port:

```text
8080
```

and is exposed on the host machine using:

```text
8081
```

Environment-specific MySQL and Redis configuration should be supplied when running the container.

---

## Database Entities

The main entities in CineLock are:

- User
- Movie
- Show
- Seat
- Booking
- Payment

Relationships between these entities are handled using Spring Data JPA and Hibernate.

---

## Testing

The application is tested using:

- Postman for REST API testing
- Swagger UI for API testing and documentation
- JUnit for application testing
- Multithreaded concurrency testing
- MySQL for persistence verification
- Redis / Memurai for temporary hold handling

---

## Concurrency Test

A multithreaded test is used to simulate multiple users trying to access the same seat simultaneously.

Java utilities such as:

```text
Thread
CountDownLatch
```

are used to trigger concurrent requests.

The test verifies that pessimistic locking prevents multiple successful bookings for the same seat.

---

## Core Concepts Used

### REST APIs

The backend exposes REST endpoints using HTTP methods such as GET, POST, and PUT.

These APIs are used to manage movies, shows, seats, bookings, payments, and authentication.

### Layered Architecture

The application follows:

```text
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
Database
```

Each layer has a separate responsibility.

### Dependency Injection

Spring Dependency Injection is used to provide required service and repository objects instead of manually creating them using `new`.

Constructor injection is used throughout the application.

### Transaction Management

`@Transactional` is used for operations that need to execute safely inside a database transaction.

This is important for booking and payment-related operations.

### Pessimistic Locking

A pessimistic write lock is used on the seat row during booking.

This prevents another transaction from modifying the same seat until the current transaction is completed.

### Redis TTL

Redis stores temporary seat hold data with an expiration time.

This allows temporary reservations to expire automatically.

### JWT Authentication

JWT tokens are generated after successful login.

The token is included with protected API requests to identify the authenticated user.

### Role-Based Authorization

Spring Security restricts APIs based on roles such as:

```text
USER
ADMIN
```

### Bean Validation

Jakarta Validation annotations such as:

```text
@NotBlank
@NotNull
@Min
@Positive
```

are used to validate request data.

### Global Exception Handling

`@RestControllerAdvice` and `@ExceptionHandler` are used to manage application errors centrally.

### Spring Data JPA

Spring Data JPA is used to communicate with MySQL through repository interfaces.

It reduces manual database boilerplate code.

### Docker

Docker packages the Spring Boot application together with its runtime environment.

This helps the application run consistently across different systems.

---

## Project Motivation

CineLock is not just a basic CRUD application.

The main focus of the project is solving a real-world concurrency problem commonly found in booking systems.

The project demonstrates:

- Concurrent request handling
- Database locking
- Transaction management
- Temporary state management using Redis
- Authentication and authorization
- REST API development
- Validation
- Exception handling
- Containerization

---

## Future Improvements

- Docker Compose for Spring Boot, MySQL and Redis
- AWS cloud deployment
- GitHub Actions CI/CD pipeline
- Refresh token support
- User-specific booking history
- Email booking confirmation
- Payment gateway integration
- Monitoring and logging
- Improved distributed locking
- Microservices architecture

---

## Author

**Rangaswamy**

Java Backend Developer

```text
Java | Spring Boot | Spring Security | JWT | Redis | MySQL | Docker
```