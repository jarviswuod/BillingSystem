# Billing Application API

A Spring Boot REST API for a simple billing system that manages customers, invoices, and payments.

## Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Lombok

## Prerequisites
- Java 17+
- PostgreSQL
- Maven

## Setup

1. Clone the repository
   git clone <your-repo-url>

2. Create a PostgreSQL database
   CREATE DATABASE billingdb;

3. Update application.properties with your credentials
   spring.datasource.url=jdbc:postgresql://localhost:5432/billingdb
   spring.datasource.username=your_username
   spring.datasource.password=your_password

4. Run the application
   ./mvnw spring-boot:run

## API Endpoints

### Customers
- POST   /api/customers
- GET    /api/customers
- GET    /api/customers/{id}
- PUT    /api/customers/{id}
- DELETE /api/customers/{id}

### Invoices
- POST   /api/invoices
- GET    /api/invoices
- GET    /api/invoices/{id}
- DELETE /api/invoices/{id}
- GET    /api/invoices/overdue

### Payments
- POST   /api/payments
- GET    /api/payments
- GET    /api/payments/{id}

### Dashboard
- GET    /api/dashboard/summary
- GET    /api/dashboard/top-customers
- GET    /api/dashboard/monthly-revenue

## Dashboard Date Filters
All dashboard endpoints accept optional query parameters:
- startDate (format: YYYY-MM-DD)
- endDate (format: YYYY-MM-DD)

Example:
GET /api/dashboard/summary?startDate=2026-01-01&endDate=2026-12-31

## Overdue Invoice Filters
- customerId
- startDate
- endDate

Example:
GET /api/invoices/overdue?customerId=1&startDate=2026-01-01&endDate=2026-12-31
