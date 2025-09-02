# BrokageFirm Spring Boot Project

This project is a simple brokerage firm backend application built with Spring Boot. It manages customers, assets, and orders, and supports basic trading operations (buy/sell/match/cancel). The application uses an H2 in-memory database and is designed to be tested easily with Postman.

## Features
- Customer, Asset, and Order management
- Buy/Sell/Match/Cancel order operations
- Asset balance and order status tracking
- H2 in-memory database for easy testing
- Ready for Postman API testing
- **Role-based authentication and authorization**

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database
- Spring Security (JWT & Basic Auth)
- Postman (for API testing)

## Authentication & Authorization

### User Types
- **Admin:**
  - Can access and manipulate all customers' data.
  - Can perform all operations, including matching orders.
- **Customer:**
  - Can only access and manipulate their own data.
  - Can only perform actions (list, create, delete orders, etc.) for their own customerId.
  - **Cannot perform match operations.**

### Passwords
- Customer passwords are stored in the `customer` table (see `data.sql`).
- When logging in or making a request, you must use the plain (unhashed) password as stored in the table.
- For customers, there are **two authentication methods** to access endpoints:
  1. **Basic Auth:** Enter your username and password in Postman using Basic Auth (Authorization tab → Type: Basic Auth).
  2. **JWT (Bearer Token):** First, POST to `/login` with your username and password to obtain a JWT token, then use the token in the `Authorization: Bearer <token>` header for subsequent requests.
- You can use either method; both are supported and optional.
- For any operation, the `customerId` in the request **must match** the `customerId` of the authenticated user (the one whose username/password or token you used to log in).
- If you try to perform an operation for another customer, you will get an authorization error.

#### Example:
- If you log in with username: `ahmet` and password: `12345` (using Basic Auth in Postman):

  - The following request will be **successful**:
    - **POST** `http://localhost:8080/orders/create`
    - **Body:**
      ```json
      {
        "customerId": "12345",
        "assetName": "AAPL",
        "orderSide": "BUY",
        "size": 10,
        "price": 500
      }
      ```

  - The following request will be **unsuccessful** (authorization error):
    - **POST** `http://localhost:8080/orders/create`
    - **Body:**
      ```json
      {
        "customerId": "67890",
        "assetName": "AAPL",
        "orderSide": "BUY",
        "size": 10,
        "price": 500
      }
      ```

  - Because you are logged in as `ahmet` (customerId `12345`), you cannot create an order for another customer (`67890`).

### Admin User
- Username: `admin`
- Password: `admin123`
- The admin can access and manipulate all data, and is the only user who can perform the match operation.

### Customer Example
- Username: `ahmet`, Password: `12345`, CustomerId: `12345`
- Username: `ayse`, Password: `67890`, CustomerId: `67890`

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
- **Type:** Basic Auth (for admin) or JWT (for customer, via /login endpoint)
- **Admin:**
  - Username: `admin`
  - Password: `admin123`
- **Customer:**
  - Username: (see data.sql, e.g. `ahmet`)
  - Password: (see data.sql, e.g. `12345`)

#### For Customers:
   
Customers can access endpoints using either of the following authentication methods:

1. **Basic Auth:**
   - In Postman, select the Authorization tab, choose Basic Auth, and enter your username and password as shown in the customer examples above.
   - You can directly access all endpoints with Basic Auth.

2. **JWT (Bearer Token):**
   - First, POST to `/login` with JSON body:
     ```json
     {
       "username": "ahmet",
       "password": "12345"
     }
     ```
     The response will contain a JWT token.
   - For all other requests, set the `Authorization` header to `Bearer <token>`.

Both methods are supported and optional; you may use whichever is more convenient for you.


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
- **Note:** Only the customer with customerId `12345` (and admin) can access this data.

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
- **Note:** Only the customer with customerId `12345` (and admin) can create an order for this customer.

#### 3. Delete Order
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/orders/delete/{orderId}`
- Replace `{orderId}` with the actual order ID (e.g. `5`).
- **Note:** Only the owner customer (or admin) can delete the order.

#### 4. Match Order
- **Method:** `PUT`
- **URL:** `http://localhost:8080/orders/match/{orderId}`
- Replace `{orderId}` with the actual order ID.
- **Note:** Only the admin user can perform this operation.

#### 5. List Assets by Customer
- **Method:** `GET`
- **URL:** `http://localhost:8080/assets/list/{customerId}`
- Replace `{customerId}` with the actual customer ID (e.g. `12345`).
- **Note:** Only the owner customer (or admin) can access this data.

## Postman Auth Settings
- For admin: Use the Authorization tab, select `Basic Auth`, and enter the admin credentials.
- For customers: First obtain a JWT from `/login`, then set the `Authorization` header to `Bearer <token>` for all requests.

## Notes
- All endpoints require authentication.
- Only the admin user can access all data and perform all operations by default.
- Customers can only access and manipulate their own data, and cannot perform match operations.
- The H2 database is in-memory and resets on each application restart.

## Example Postman Collection
You can create a Postman collection with the above endpoints and set the Basic Auth or Bearer Token at the collection level for convenience.

---

For any issues, please check the code comments or contact the project maintainer.
