🛞 TyrePlus Dealer API
Production-grade Spring Boot 3.4 backend for the Tyre Dealer application.

🏗 Architecture & Tech Stack
Java 21 with Virtual Threads enabled for high-concurrency lead processing.

Domain-Driven Design (DDD): Logic is isolated from framework "noise."

PostgreSQL 16: Utilizing Pessimistic Locking for wallet and lead purchase safety.

Flyway: Automated database versioning and migrations.

📂 Project Structure

src/main/java/com/tyreplus/dealer/
├── domain/          # Core "Brain": Entities (Lead, Wallet), Value Objects, Repository Interfaces
├── application/     # Orchestration: Services (Auth, Purchase, Dashboard), DTOs, Exceptions
├── infrastructure/  # Technical Muscle: JPA Adapters, Security (JWT/OTP), SMS, Config
└── web/             # Entry Points: REST Controllers, Global Exception Handlers

🚀 Getting Started

1. Database & Environment
Ensure Docker is running, then start the database:

docker-compose up -d

2. Running with Sample Data
To help with frontend development, the app includes a dev profile that seeds the database with sample leads.

mvn spring-boot:run -Dspring-boot.run.profiles=dev

The app will be available at: http://localhost:8080

🔐 Authentication Flow
This app uses a Passwordless OTP Flow.

Request OTP: POST /api/v1/auth/otp (Request: mobile)

Verify & Login: POST /api/v1/auth/login (Request: mobile, otp)

Authorize: Copy the token from the login response. Add it to all subsequent requests as a Bearer token: Authorization: Bearer <your_token>

🎯 Main API Endpoints

Feature,Method,Endpoint,Description
Dashboard,GET,/api/v1/dealer/dashboard,Wallet balance + Today's Stats + Recent Leads
Leads,GET,/api/v1/leads,List available leads in the marketplace
Purchase,POST,/api/v1/leads/{id}/buy,Buy a lead (Deducts wallet balance)
Status,PUT,/api/v1/leads/{id}/status,Update to CONVERTED or CANCELLED
Wallet,GET,/api/v1/dealer/wallet,Balance and full transaction history
Profile,PUT,/api/v1/dealer/profile,"Update business hours, address, and info"

🛠 Development Notes
Transactions: All money-related operations use @Lock(LockModeType.PESSIMISTIC_WRITE) to prevent double-spending.

Validation: Failed requests return a structured JSON with a timestamp, status, and specific message.

Virtual Threads: Enabled via spring.threads.virtual.enabled=true. Avoid synchronized blocks in new code; use ReentrantLock if necessary.