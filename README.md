# Interview Coding Task: Write Unit Tests for OrderService

## Task

You are given a small order processing domain implemented in Java.

Your task is to write pure unit tests for the `OrderService` class.

## Requirements

- Use JUnit 5
- Use Mockito
- Do not use Spring Boot Test
- Do not load Spring context
- Test only the `OrderService` class in isolation
- Mock all external dependencies
- Cover both success and failure scenarios
- Verify important interactions with dependencies

## What we want to evaluate

We want to see whether you can:

- identify test scenarios from business rules
- write focused unit tests
- mock dependencies correctly
- verify method calls and non-calls
- test exceptions properly
- avoid unnecessary framework usage
- keep tests readable and maintainable

---

## Production Code

Production code is under:

- `src/main/java/com/example/orders`

### Classes included

- `OrderService`
- `ProductRepository`
- `OrderRepository`
- `PaymentGateway`
- `NotificationService`
- `Product`
- `OrderRequest`
- `Order`
- `OrderResult`
- `PaymentResult`
- `ProductNotFoundException`
- `InactiveProductException`
- `InsufficientStockException`
- `PaymentFailedException`

---

## Instructions for the Candidate

Write unit tests for `OrderService#placeOrder`.

Your test suite should cover the business behavior of the service as thoroughly as possible.

At minimum, think about:

- input validation
- missing product
- inactive product
- insufficient stock
- payment failure
- successful order creation
- stock update after successful payment
- whether alert notification should be sent or not depending on order total
- whether dependencies are called or not called in the correct scenarios

You may use:

- `@Mock`
- `@InjectMocks`
- `when(...)`
- `thenReturn(...)`
- `verify(...)`
- `never()`
- `times(...)`
- `any()`
- `argThat(...)`
- `assertThrows(...)`

Do not modify the production code.

---

## Expected Deliverable

Provide a test class named:

- `OrderServiceTest`

using JUnit 5 + Mockito.

A starter test file is available at:

- `src/test/java/com/example/orders/OrderServiceTest.java`

---

## Optional Follow-up Discussion

Be prepared to explain:

- why this is a unit test and not an integration test
- why dependencies are mocked
- which scenarios are most important
- how you decide what to verify
- which assertions are about returned data vs side effects

---

## Running Unit Tests

```bash
mvn test
```

---

## Running the App

This repository also includes a runnable Spring Boot app for manual testing.

- Main class: `src/main/java/com/example/orders/OrderApplication.java`
- Properties file: `src/main/resources/application.properties`

Start app:

```bash
mvn spring-boot:run
```

### Seed Data

The app seeds:

- `10` products (`p-100` ... `p-1000`)
- `10` historical orders (`id: 1 ... 10`)
- `10` customer payment profiles (`cust-101` ... `cust-110`)

Notes:

- Product stock is adjusted to remain consistent with seeded order history.
- High-value seeded orders trigger notification logs on startup.
- If `customerId` starts with `FAIL` (configurable in `application.properties`), payment is still simulated as failed.

### API

Endpoints:

```bash
# create order (uses OrderService business flow)
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"cust-1","productId":"p-100","quantity":2}'

# list all orders
curl http://localhost:8081/api/orders

# get one order
curl http://localhost:8081/api/orders/1

# replace order (PUT: all fields required)
curl -X PUT http://localhost:8081/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{"customerId":"cust-2","productId":"p-200","quantity":1}'

# patch order (PATCH: partial fields allowed)
curl -X PATCH http://localhost:8081/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity":3}'

# delete order
curl -X DELETE http://localhost:8081/api/orders/1
```

You can inspect seeded orders immediately with:

```bash
curl http://localhost:8081/api/orders
```
