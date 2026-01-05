# 🚀 TyrePlus Dealer App Backend
A robust Spring Boot backend for the TyrePlus Dealer ecosystem, featuring JWT Authentication, Lead Discovery, and Wallet Management with Payment integration.

## 🛠 Architecture & Tech Stack
- **Language:** Java 21 (with Virtual Threads for high concurrency)
- **Framework:** Spring Boot 3.4
- **Security:** Spring Security + JWT
- **Database:** PostgreSQL 16  
  - Pessimistic Locking for wallet and lead purchase safety
- **Cache/Store:** Redis 7
- **Design Pattern:** Domain-Driven Design (DDD) — isolates business logic from framework “noise”
- **Database Migrations:** Flyway for automated versioning
- **Container:** Docker & Docker Compose
