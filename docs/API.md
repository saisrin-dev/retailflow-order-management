# REST API

Base URL locally: `http://localhost:8080`. JSON request/response bodies. Import `postman/RetailFlow.postman_collection.json` for examples. There is no auth in the sandbox.

| Method | Path | Purpose |
| --- | --- | --- |
| GET | /actuator/health | Health status |
| GET | /api/products | Full seeded catalog |
| POST | /api/products/{id}/restock | Add a positive quantity, at most 10,000 |
| GET | /api/orders | Orders sorted newest first |
| POST | /api/orders | Create and reserve stock; requires Idempotency-Key header |
| PATCH | /api/orders/{id}/status | Allowed fulfillment transition |
| GET | /api/dashboard | Aggregate counts and non-cancelled booked value |

## Create order

Header: `Idempotency-Key: retailflow-demo-order-001`.

```json
{"retailer":"Maple Street Market","items":[{"productId":1,"quantity":2},{"productId":4,"quantity":1}]}
```

Response: 201 and an order with ID, retailer, status, creation time, decimal total, price snapshots, and activity entries. Replay with the same key and matching normalized payload returns that order without another reservation (also 201). A changed payload with that key returns 409. For a new order, generate a new key. Angular retains the key across failed retries until the cart/payload changes or the request succeeds.

Retailer: required, max 80 characters. Items: 1–50 lines. Product IDs: positive and existing. Quantity: 1–1,000 per normalized product. Repeated product lines are merged. The total comes from database prices, never from browser-submitted prices.

## Update status

```json
{"status":"CONFIRMED"}
```

| Current | Allowed next |
| --- | --- |
| PLACED | CONFIRMED, CANCELLED |
| CONFIRMED | SHIPPED, CANCELLED |
| SHIPPED | DELIVERED |
| DELIVERED | none |
| CANCELLED | none |

Sending the current status again returns the order without a duplicate event. Cancellation restores stock only on the first transition. A shipped order cannot be cancelled.

## Restock

```json
{"quantity":20}
```

## Error contract

Expected validation and business errors use `{"message":"Human-readable reason"}`.

- 400: invalid input, absent/invalid request key, malformed JSON/status.
- 404: unknown product/order.
- 409: insufficient stock, illegal transition, mismatched key or simultaneous duplicate insert.

The complete app uses same-origin requests; the Angular development server proxies `/api` and `/actuator` to port 8080.
