# 💳 Wallet Payment System

A secure, backend-only **wallet and payments service** built with **Spring Boot 3 / Java 21**. It supports user signup/login with JWT authentication, per-user wallets, top-up/withdraw/transfer operations with transaction history, admin controls to freeze/unfreeze accounts, and asynchronous email notifications powered by **RabbitMQ**.

---

## ✨ Features

- **JWT-based authentication** — stateless signup/login with Spring Security
- **Role-based access control** — `ROLE_USER` vs `ROLE_ADMIN`, enforced via `@PreAuthorize`
- **Wallet management** — auto-created wallet on signup, balance top-up, withdrawal, and peer-to-peer transfer
- **Transaction history** — every wallet operation is logged (`TOPUP`, `WITHDRAW`, `TRANSFER`) and the last 5 transactions are returned with wallet details
- **Admin controls** — list all users, freeze/unfreeze a user's wallet (frozen users can't send or receive funds)
- **Asynchronous notifications** — wallet events are published to RabbitMQ and consumed to send email alerts via Spring Mail
- **API docs** — Swagger / OpenAPI UI via springdoc
- **Monitoring** — Spring Boot Actuator endpoints
- **Encrypted passwords** — BCrypt via Spring Security's `PasswordEncoder`

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.5.5 |
| Security | Spring Security, JWT (`jjwt` 0.11.5) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Messaging | RabbitMQ (Spring AMQP) |
| Email | Spring Mail (SMTP) |
| API Docs | springdoc-openapi (Swagger UI) |
| Build Tool | Maven |
| Utilities | Lombok, Jakarta Bean Validation |

## 📁 Project Structure

```
Wallet-Payment-System/
└── src/main/java/com/walletsystem/project/Wallet/Payment/System/
    ├── config/          # Security, RabbitMQ, and message-converter beans
    ├── controller/       # AuthController, UserController, WalletController
    ├── dto/               # Request/response payloads
    ├── entity/            # User, Wallet, WalletTransaction
    ├── repository/        # Spring Data JPA repositories
    ├── security/          # JwtUtil, JwtAuthFilter
    └── service/            # Auth & Wallet business logic, notification producer/consumer
```

## ✅ Prerequisites

- Java 21+
- Maven 3.9+ (or use the bundled `./mvnw`)
- PostgreSQL running locally (or accessible)
- RabbitMQ running locally (or accessible) — required at startup since messaging beans are auto-configured
- An SMTP account (e.g. a Gmail account with an [App Password](https://support.google.com/accounts/answer/185833)) if you want email notifications to work

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/cbum-2023/Wallet_Payment_System.git
cd Wallet_Payment_System/Wallet-Payment-System
```

### 2. Configure the application

Edit `src/main/resources/application.properties` (or override via environment variables) with your own credentials — **do not commit real secrets**:

```properties
spring.application.name=Wallet-Payment-System
server.port=8082

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/wallet_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# SMTP (used for wallet-event email alerts)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Create the database beforehand, e.g.:

```sql
CREATE DATABASE wallet_db;
```

### 3. Run the app

```bash
./mvnw spring-boot:run
```

The API starts on **`http://localhost:8082`**.

### 4. Explore the API docs

Once running, Swagger UI is available at:

```
http://localhost:8082/swagger-ui.html
```

## 📌 API Reference

### Auth (`/api/auth`) — public

| Method | Endpoint | Body | Description |
|---|---|---|---|
| POST | `/api/auth/signup` | `{ "username", "email", "password" }` | Register a new user; a wallet is auto-created with a zero balance |
| POST | `/api/auth/login` | `{ "usernameOrEmail", "password" }` | Authenticate and receive a JWT (`accessToken`, `tokenType: Bearer`) |

### Wallet (`/api/wallet`) — requires `Authorization: Bearer <token>`

| Method | Endpoint | Params | Description |
|---|---|---|---|
| POST | `/api/wallet/topup/{userId}` | `amount` (query) | Add funds to a user's wallet |
| POST | `/api/wallet/withdraw/{userId}` | `amount` (query) | Withdraw funds from a user's wallet |
| POST | `/api/wallet/transfer` | `fromUserId`, `toUserId`, `amount` (query) | Transfer funds between two users' wallets |
| GET | `/api/wallet/balance/{userId}` | — | Get current wallet balance |
| GET | `/api/wallet/details/{userId}` | — | Get balance plus the last 5 transactions |

Frozen accounts receive `403 Forbidden` on top-up, withdraw, and transfer.

### Users (`/api/users`) — requires `ROLE_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all registered users |
| PUT | `/api/users/{id}/freeze` | Freeze a user's wallet (blocks send/receive) |
| PUT | `/api/users/{id}/unfreeze` | Unfreeze a user's wallet |

## 🔐 Security Model

- Passwords are hashed with **BCrypt** before storage.
- On login, a **JWT** is issued containing the username and role; it must be sent as `Authorization: Bearer <token>` on subsequent requests.
- `/api/auth/**` is public; `/api/users/**` is restricted to `ROLE_ADMIN`; everything else requires a valid token.
- Sessions are stateless (`SessionCreationPolicy.STATELESS`) — no server-side session state.

## 📨 Async Notifications

Wallet events are published to a durable RabbitMQ queue (`notificationQueue`, bound to `notificationExchange` via the `notification.email` routing key). A `@RabbitListener` consumer picks up each message and sends an HTML email via Spring Mail — so email delivery never blocks the request thread.

## 🧪 Testing

```bash
./mvnw test
```

## 🗺️ Roadmap Ideas

- Refresh tokens / token revocation
- Pagination for transaction history
- Rate limiting on wallet operations
- Dockerfile + docker-compose for Postgres, RabbitMQ, and the app

## 👨‍💻 Author

**Shivam** ([@cbum-2023](https://github.com/cbum-2023)) — Final year student, IIIT Ranchi

## 🤝 Contributing

Issues and PRs are welcome — fork the repo, create a feature branch, and open a pull request.

## 📄 License

No license file is currently included in this repository. Add one (e.g. MIT) if you intend for others to reuse this code.
