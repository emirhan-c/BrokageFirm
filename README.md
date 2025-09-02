# BrokageFirm Spring Boot Project

This project is a simple brokerage firm backend application built with Spring Boot. It manages customers, assets, and orders, and supports basic trading operations (buy/sell/match/cancel). The application uses an H2 in-memory database and is designed to be tested easily with Postman.

## Features
- Customer, Asset, and Order management
- Buy/Sell/Match/Cancel order operations
- Asset balance and order status tracking
- H2 in-memory database for easy testing
- Ready for Postman API testing

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database
- Spring Security (Basic Auth, see below)
- Postman (for API testing)

## Getting Started

### 1. Clone the Repository
```
git clone <your-repo-url>
cd BrokageFirm
```

### 2. Build and Run the Project
You can use Maven Wrapper:
```
./mvnw spring-boot:run
```
Or with Maven:
```
mvn spring-boot:run
```

The application will start on: `http://localhost:8080`

### 3. H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (leave blank)

### 4. Initial Data
Sample data is loaded from `src/main/resources/data.sql` at startup. You can modify this file to add your own test data.

## API Usage with Postman

### Authentication
- **Type:** Basic Auth
- **Username:** `admin`
- **Password:** `admin123`

> **Note:** All endpoints require authentication. Only the admin user is available by default. (If you add customer auth, use the relevant username/password.)

### Common Endpoints

#### 1. List Orders
- **Method:** `GET`
- **URL:** `http://localhost:8080/orders/list`
- **Body (raw JSON):**
```
{
  "customerId": "12345",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

#### 2. Create Order
- **Method:** `POST`
- **URL:** `http://localhost:8080/orders/create`
- **Body (raw JSON):**
```
{
  "customerId": 12345,
  "assetName": "AAPL",
  "orderSide": "BUY",
  "size": 10.0,
  "price": 150.0
}
```

#### 3. Delete Order
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/orders/delete/{orderId}`
- Replace `{orderId}` with the actual order ID (e.g. `5`).

#### 4. Match Order
- **Method:** `PUT`
- **URL:** `http://localhost:8080/orders/match/{orderId}`
- Replace `{orderId}` with the actual order ID.

#### 5. List Assets by Customer
- **Method:** `GET`
- **URL:** `http://localhost:8080/assets/list/{customerId}`
- Replace `{customerId}` with the actual customer ID (e.g. `12345`).

## Postman Auth Settings
- Go to the Authorization tab in Postman.
- Select `Basic Auth`.
- Enter the username and password (see above).
- For each request, make sure the Authorization header is set.

## Notes
- All endpoints require authentication.
- Only the admin user can access all data and perform all operations by default.
- If you add customer authentication, each customer will only be able to access their own data.
- The H2 database is in-memory and resets on each application restart.

## Example Postman Collection
You can create a Postman collection with the above endpoints and set the Basic Auth credentials at the collection level for convenience.

---

For any issues, please check the code comments or contact the project maintainer.

