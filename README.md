# Finance Manager API

RESTful API for personal finance management, featuring secure authentication, account management, transactions, and financial reports.

---

## Deploy

https://finance-manager-api-gb25.onrender.com

---

## Documentation (Swagger)

https://finance-manager-api-gb25.onrender.com/swagger-ui/index.html

---

## About the Project

**Finance Manager API** is a complete backend system for personal financial management, allowing users to manage accounts, track transactions, and generate financial insights.

This project was built with a focus on:

* Clean architecture
* Security best practices
* Scalability
* Domain-oriented design (DDD-like)

---

## Technologies

* Java 21
* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* PostgreSQL
* Docker
* Maven
* Swagger (OpenAPI 3)

---

## Authentication

The API uses **JWT (JSON Web Token)** for authentication.

### Authentication flow

1. Login:

```http
POST /auth/login
```


2. Response:

```json
{
  "token": "jwt_token"
}
```

3. Use the token in requests:

```http
Authorization: Bearer jwt_token
```

---

## Endpoints

### Authentication

* `POST /auth/login`
* `POST /auth/register`
* `POST /auth/logout`
* `POST /auth/refresh`

---

### Accounts

* `GET /accounts` → List accounts
* `POST /accounts` → Create account
* `GET /accounts/{id}` → Get account by ID
* `PATCH /accounts/{id}` → Update account
* `DELETE /accounts/{id}` → Delete account

---

### Transactions

* `GET /accounts/{accountId}/transactions`
* `POST /accounts/{accountId}/transactions`
* `GET /accounts/{accountId}/transactions/{id}`
* `DELETE /accounts/{accountId}/transactions/{id}`

---

### Dashboard

* `GET /dashboard` → Financial overview

---

### Reports

* `GET /reports/monthly`
* `GET /reports/yearly`

---

## Business Rules

* Transactions directly impact account balance
* Clear distinction between **income** and **expense**
* Ownership validation (users can only access their own data)
* Automatic balance updates
* Consistency across update and delete operations

---

## Running locally

### 1. Clone the repository

```bash
git clone https://github.com/LuccaGarDal/finance-manager-api
cd finance-manager-api
```

---

### 2. Configure `.env`

```env
DB_URL=jdbc:postgresql://localhost:5433/finance_manager
DB_USERNAME=finance_db
DB_PASSWORD=password

JWT_SECRET=your_secret
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=172800000
```

---

### 3. Start database with Docker

```bash
docker-compose up -d
```

---

### 4. Run the application

```bash
./mvnw spring-boot:run
```

---

## Docker

```bash
docker build -t finance-api .
docker run -p 8080:8080 finance-api
```

---

## Architecture

```text
src/
 ├── config
 ├── controller
 ├── dto
 ├── entity
 ├── exceptions
 ├── infra
 ├── mapper
 ├── repository
 ├── security
 └── service
```

---

## API Testing

You can test the API using:

* Swagger UI
* Postman
* Insomnia

---

## Roadmap

### Completed

* JWT Authentication
* Accounts CRUD
* Transactions CRUD
* Financial dashboard
* Monthly and yearly reports
* Pagination and filtering
* Transaction categories
* Refresh token

---

### Future Improvements

* Financial goals
* Spending alerts
* Installments
* Transfers between accounts
* Investments

---

## Backlog (Professional Overview)

### Phase 1 — Authentication

* User registration and login
* JWT implementation
* BCrypt password hashing
* Exception handling

---

### Phase 2 — Accounts

* Full CRUD
* Ownership validation

---

### Phase 3 — Transactions

* Income vs Expense
* Account relationships
* Balance rules

---

### Phase 4 — Financial Rules

* Automatic balance updates
* Reversal on delete/update

---

### Phase 5 — Pagination & Filters

* Pagination
* Date and type filters

---

### Phase 6 — Categories

* Transaction categorization

---

### Phase 7 — Dashboard

* Consolidated financial view

---

### Phase 8 — Reports

* Monthly and yearly reports

---

### Phase 9 — Security

* Rate limiting
* Refresh tokens
* Logging

---

## Environment Variables (Production)

The following environment variables are required to run the application in production:

```env
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<your_database_user>
DB_PASSWORD=<your_database_password>

JWT_SECRET=<your_jwt_secret>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=172800000
```

---

## Author

Lucca Garcia

---

## License

This project is licensed under the MIT License.
