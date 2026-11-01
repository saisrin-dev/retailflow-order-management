# Learn and explain RetailFlow

## Seven focused sessions

| Day | Work | Demonstrate |
| --- | --- | --- |
| 1 | Start the app, walk every screen, read README/API | Place and cancel an order |
| 2 | Read Product, PurchaseOrder, DTO records | Explain fields, records, classes, enums, BigDecimal |
| 3 | Read Controller and validation annotations | Send valid/invalid JSON from Postman |
| 4 | Follow RetailService.create line by line | Explain transactions, maps, streams, locking |
| 5 | Read status handling and tests | Show terminal states and stock restoration |
| 6 | Read Angular service, signals, template | Explain data binding, HTTP, state, error UI |
| 7 | Push, read CI logs, deploy Docker | Present the 3-minute demo using your real URL |

## Feature naming

| Feature | Classes / methods | Meaning |
| --- | --- | --- |
| Catalog browsing | Product, ProductRepository, RetailService.products | Store and retrieve available items |
| Order placement | CreateOrder, RetailService.create | Validate, reserve stock, persist order |
| Fulfillment | PurchaseOrder.Status, RetailService.status | Enforce legal status transitions |
| Replenishment | Restock, RetailService.restock | Increase inventory safely |
| Dashboard | Dashboard, RetailService.dashboard | Derive live operational totals |
| Browser communication | ApiService | Typed Angular HTTP methods |

## Java concepts in this project

- **Class**: `RetailService` groups business behavior. `Product` represents a persistent entity.
- **Method**: `create(...)` accepts input and performs one workflow.
- **Constructor injection**: Spring supplies repository dependencies through the service constructor.
- **Interface**: `ProductRepository` declares operations; Spring generates the implementation.
- **Record**: API models are compact immutable request/response containers.
- **Enum**: `Status` restricts valid order states.
- **Map**: quantities are combined by product ID; `TreeMap` gives deterministic lock order.
- **Exception**: business failures are mapped to HTTP status and a readable message.
- **Transaction**: order persistence and stock reservation succeed or fail together.
- **Pessimistic lock**: competing requests wait on the same product/order row instead of overselling or double-cancelling.
- **Optimistic version**: Product has a version column for additional stale-write detection.
- **Stream**: the dashboard aggregates active booked value; orders are sorted and mapped into DTOs.
- **JPA annotation**: `@Entity`/`@Id` define persistence. JPA entities use explicit field access in this compact version; API DTOs keep persistence-only fields off the wire.

## Interview answer you can adapt after using it

“I built RetailFlow as an independent Java full stack project inspired by retailer order workflows. The Angular UI lets a user create orders, track fulfillment, and restock products. Spring Boot validates requests and calculates prices on the server. I used transactions and row locks so order creation reserves stock safely. An idempotency key prevents a retried request from creating a second reservation. I wrote integration tests for cancellation, invalid transitions, and duplicate requests, then added a Docker build and GitHub Actions checks.”

Only say you deployed it, enabled PostgreSQL, or passed particular checks after doing so. Describe this as a personal project, separate from employer work.

## Questions to practice

1. Why BigDecimal rather than double for server-side money?
2. What happens if line one reserves stock and line two is unavailable?
3. What does a database transaction guarantee here?
4. Why must the server recalculate totals?
5. How do two simultaneous orders avoid overselling?
6. How is a retry different from a second purchase?
7. Why snapshot the original unit price on an order line?
8. Why is cancellation not permitted after shipping?
9. What is the difference between local H2 and persistent PostgreSQL?
10. Why can GitHub Pages run your portfolio but not your Java API?

## Next improvements — not included in v1

1. Add JWT authentication and separate ADMIN/RETAILER roles with tenant isolation.
2. Add Flyway migrations and PostgreSQL Testcontainers tests.
3. Add pagination, filtering, rate limits, and structured application logging.
4. Introduce a transactional outbox before publishing Kafka order events.
5. Add Redis catalog caching with explicit invalidation after stock changes.
6. Add browser automation for the full placement-to-delivery flow.

Start with the current app. Add one capability at a time and explain its tradeoffs rather than claiming every resume technology in the first version.
