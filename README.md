# Notification Service

A small Spring Boot backend service that accepts notification requests and delivers them through a simulated notification provider.

The project focuses on clear API design, notification lifecycle management, validation, persistence, failure handling, and automated testing.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Jakarta Bean Validation
- JUnit 5
- Mockito
- Maven

## API Endpoints

### Create Notification

POST /notifications

Request:

    {
      "recipient": "test@gmail.com",
      "message": "Your order has been shipped",
      "channel": "EMAIL"
    }

Successful response:

    {
      "id": "generated-uuid",
      "recipient": "test@gmail.com",
      "message": "Your order has been shipped",
      "channel": "EMAIL",
      "status": "SENT",
      "createdAt": "2026-10-08T07:30:00Z",
      "updatedAt": "2026-10-08T07:30:01Z"
    }

### Get Notification

GET /notifications/{id}

Returns the notification and its current status.

## Notification Lifecycle

    PENDING
       |
       v
    PROCESSING
       |
       +------> SENT
       |
       +------> FAILED

A notification starts in PENDING state.

During delivery it moves to PROCESSING.

After the provider completes:

- Successful delivery → SENT
- Provider failure → FAILED

## Architecture

    Client
      |
      v
    NotificationController
      |
      v
    NotificationService
      |
      +---------------------> NotificationRepository
      |                              |
      |                              v
      |                             H2
      |
      +---------------------> NotificationProvider
                                     |
                                     v
                           MockNotificationProvider

The service depends on the NotificationProvider interface rather than directly depending on the provider implementation. This keeps the delivery mechanism replaceable.

## Project Structure

    src
    ├── main
    │   ├── java
    │   │   └── com.example.notificationservice
    │   │       ├── controller
    │   │       │   └── NotificationController.java
    │   │       ├── dto
    │   │       │   ├── ApiError.java
    │   │       │   ├── NotificationRequest.java
    │   │       │   └── NotificationResponse.java
    │   │       ├── entity
    │   │       │   ├── Notification.java
    │   │       │   ├── NotificationChannel.java
    │   │       │   └── NotificationStatus.java
    │   │       ├── exception
    │   │       │   ├── GlobalExceptionHandler.java
    │   │       │   └── NotificationNotFoundException.java
    │   │       ├── provider
    │   │       │   ├── MockNotificationProvider.java
    │   │       │   └── NotificationProvider.java
    │   │       ├── repository
    │   │       │   └── NotificationRepository.java
    │   │       └── service
    │   │           └── NotificationService.java
    │   └── resources
    │       └── application.properties
    │
    └── test
        └── java
            └── com.example.notificationservice
                ├── controller
                │   └── NotificationControllerTest.java
                └── service
                    └── NotificationServiceTest.java

## Validation

Notification requests are validated using Jakarta Bean Validation.

Required fields:

- recipient
- message
- channel

Invalid requests return:

    400 Bad Request

Example:

    {
      "recipient": "",
      "message": "",
      "channel": null
    }

## Error Handling

The service handles the following cases:

### Invalid Request

    400 Bad Request

### Notification Not Found

    404 Not Found

### Provider Failure

The notification is persisted with:

    status = FAILED

This allows clients to inspect the final state of a notification after a delivery failure.

## Mock Notification Provider

The project uses a simulated notification provider instead of integrating with a real email or SMS service.

For demonstration purposes, the mock provider can simulate a failure when the recipient is:

    fail@test.com

This allows the notification lifecycle and failure handling to be tested deterministically.

## Testing

The project contains tests for:

- Successful notification creation
- Provider failure
- Notification retrieval
- Notification not found
- Request validation
- Controller success responses
- Controller error responses

Run all tests with:

    mvn clean test

## Running Locally

Make sure Java 21 and Maven are installed.

Start the application with:

    mvn spring-boot:run

The application runs on:

    http://localhost:8086

## Example POST Request

    POST http://localhost:8086/notifications
    Content-Type: application/json

    {
      "recipient": "test@gmail.com",
      "message": "Your order has been shipped",
      "channel": "EMAIL"
    }

## Example Failure Request

    POST http://localhost:8086/notifications
    Content-Type: application/json

    {
      "recipient": "fail@test.com",
      "message": "This should fail",
      "channel": "EMAIL"
    }

The response will contain:

    {
      "status": "FAILED"
    }

## H2 Database

The project uses an in-memory H2 database for simplicity.

H2 console:

    http://localhost:8086/h2-console

Database URL:

    jdbc:h2:mem:notificationdb

The database is recreated when the application starts.

## Assumptions

- The notification provider is simulated rather than connected to a real email or SMS service.
- H2 is used as the persistence layer for this first iteration.
- Notification delivery is synchronous.
- A notification request is persisted before delivery is attempted.
- The notification status represents the current delivery state.

## Tradeoffs

The implementation intentionally keeps the service small and focuses on clear API contracts, state management, failure handling, persistence, and testing.

Synchronous delivery was chosen to keep the first version simple and easy to reason about.

A production notification platform would require additional infrastructure for asynchronous processing, retries, monitoring, and external provider integrations.

## Intentionally Left Out

The following were intentionally not implemented:

- Real email/SMS provider integration
- Kafka or another message broker
- Retry queues
- Authentication and authorization
- Rate limiting
- Distributed tracing
- Production database configuration
- Multiple notification service instances
- Advanced monitoring and observability

These features can be added as the service evolves.

## What I Would Do Next

With more time, I would consider:

1. Asynchronous notification delivery using a message broker.
2. Retry handling with exponential backoff.
3. A production database such as PostgreSQL.
4. Provider-specific implementations for email and SMS.
5. Idempotency for duplicate notification requests.
6. Metrics and health checks.
7. Distributed tracing.
8. Authentication and authorization.

## Design Decisions

### Records for DTOs

Request and response DTOs use Java records because they are immutable, concise data carriers and reduce unnecessary boilerplate.

### Enum for Channel and Status

Notification channel and lifecycle status use enums because both represent a fixed set of valid values.

### Provider Interface

The service depends on the NotificationProvider interface rather than a concrete provider implementation. This keeps the business logic independent of the external delivery mechanism.

### Global Exception Handling

@RestControllerAdvice centralizes API error handling so controllers remain focused on HTTP request handling.

### H2

H2 was selected because the project is intentionally small and provides a simple persistence layer for this initial implementation.