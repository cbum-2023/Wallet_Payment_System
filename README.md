💳 Wallet Payment System

A secure and scalable wallet-based payment system built with Spring Boot, Hibernate/JPA, and PostgreSQL, designed to handle user authentication, wallet balance management, and transaction operations.

⚡ Features

✅ User Authentication & Authorization (JWT-based)
✅ Role-based Access (Admin/User separation)
✅ Wallet Management – check balance, deposit, withdraw
✅ Secure Payments with transaction history
✅ RESTful APIs for smooth integration
✅ PostgreSQL as the database (SQL ready)
✅ Spring Data JPA + Hibernate for ORM
✅ BCrypt password encryption for security

🛠️ Tech Stack

Backend: Java, Spring Boot

Database: PostgreSQL, SQL

ORM: Hibernate, Spring Data JPA

Auth: JWT, Spring Security

Build Tool: Maven

Tools: IntelliJ IDEA, Git, GitHub

🚀 Getting Started
1. Clone the repo
git clone https://github.com/cbum-2023/Wallet-Payment-System.git
cd Wallet_Payment_System

2. Configure PostgreSQL

Create a database (e.g. wallet_db).

Update application.properties with your DB credentials.

spring.datasource.url=jdbc:postgresql://localhost:5432/wallet_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

3. Run the app
mvn spring-boot:run

📌 API Endpoints
Method	Endpoint	Description
POST	/api/auth/register	Register new user
POST	/api/auth/login	Login user & get JWT token
GET	/api/wallet/balance	Get wallet balance
POST	/api/wallet/deposit	Deposit amount
POST	/api/wallet/withdraw	Withdraw amount
📸 Screenshots (Optional)
👨‍💻 Author

cbum-2023 (Shivam)
Pre-Final Year @ IIIT Ranchi | Full Stack Developer (Spring Boot + MERN)

⭐ Contribute

Want to improve this project? Fork it and create a PR 🚀
