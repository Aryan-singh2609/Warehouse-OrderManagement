# API Test Flow

## Base URLs

- App base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Auth Model

- The API uses session auth with the `JSESSIONID` cookie.
- Call `POST /data/login` first and reuse the cookie for all protected APIs.
- Most APIs require login.
- `User` and `Picker` creation/update/delete have stricter role checks.
- Pickers now log in with their own `PICKER` role through the same `/data/login` endpoint.

## Seeded Users

- Super admin: `Admin@Example.com` / `Admin@123`
- Associate: `associate@example.com` / `Associate@123`

## Recommended Test Order

1. Login as super admin.
2. Create one admin user.
3. Create one associate user whose email will also be used for a picker.
4. Create one picker using that associate email.
5. Create one client.
6. Create one fulfillment center.
7. Create one product for that client + FC.
8. Create one or more orders.
9. Run order lifecycle tests.
10. Run batch lifecycle tests.
11. Run delete and failure-path tests last.

## Reusable Test Data

Use unique suffixes like `20260501` to avoid duplicate-data failures.

```json
{
  "adminUser": {
    "name": "QA Admin",
    "email": "qa.admin.20260501@example.com",
    "password": "AdminPass123!",
    "role": "ADMIN"
  },
  "associateUser": {
    "name": "QA Associate",
    "email": "qa.associate.20260501@example.com",
    "password": "AssociatePass123!",
    "role": "ASSOCIATE"
  },
  "picker": {
    "name": "QA Associate",
    "email": "qa.associate.20260501@example.com",
    "employeeId": "PK-20260501",
    "password": "PickerPass123!"
  },
  "client": {
    "name": "Aarav Shah",
    "organisationName": "Northwind Retail 20260501",
    "organisationAddress": "221B Baker Street, London",
    "email": "ops.20260501@northwind.example",
    "phone": "+44-20-5555-0101"
  },
  "fc": {
    "warehouseId": "WH-20260501",
    "fcId": "FC-20260501",
    "location": "Bengaluru South Hub",
    "latitude": 12.9716,
    "longitude": 77.5946
  },
  "product": {
    "name": "USB Barcode Scanner 20260501",
    "productId": "PRD-20260501",
    "sku": "SKU-20260501",
    "description": "Handheld USB barcode scanner",
    "price": 2499.99,
    "quantity": 150
  }
}
```

## Session Setup

### 1. Login as super admin

- Method: `POST`
- URL: `http://localhost:8080/data/login`
- Body:

```json
{
  "email": "Admin@Example.com",
  "password": "Admin@123"
}
```

- Expected: `200 OK`, response contains `user`, cookie contains `JSESSIONID`
- Negative cases:
  - Wrong password -> `401`
  - Invalid email format -> `400`
  - Missing email/password -> `400`

### 2. Logout

- Method: `POST`
- URL: `http://localhost:8080/data/logout`
- Expected: `204 No Content`
- Negative case:
  - No active session -> session may already be invalid; verify behavior in your environment

## Full Endpoint Coverage

### Authentication

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Login | `POST` | `/data/login` | valid super admin login, valid associate login, wrong password `401`, invalid body `400` |
| Logout | `POST` | `/data/logout` | logout after login, retry protected API after logout should return `401` |

### Users

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Register user | `POST` | `/data/register` | super admin creates admin `200`, super admin creates associate `200`, admin tries to create admin but created role should stay restricted by service rules, duplicate email `400`, no login `401`, associate session `403`, super admin email mismatch with `SUPER_ADMIN` role `400` |
| List users | `GET` | `/data/users` | super admin `200`, admin `200`, associate `403`, no login `401` |
| Update user | `PUT` | `/data/users/{id}` | super admin updates admin `200`, admin updates associate `200`, admin updates super admin `403`, invalid email `400`, duplicate email `400`, unknown id `404` |
| Delete user | `DELETE` | `/data/users/{id}` | delete normal user `204`, delete unknown user `404`, delete super admin `403`, associate session `403` |

### Clients

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create client | `POST` | `/data/clients` | valid payload `200`, duplicate email `400`, invalid email `400`, no login `401` |
| List clients | `GET` | `/data/clients` | logged-in user `200`, no login `401` |
| Update client | `PUT` | `/data/clients/{id}` | valid update `200`, duplicate email `400`, unknown id `404`, no login `401` |
| Delete client | `DELETE` | `/data/clients/{id}` | unused client `204`, client linked to product `400`, client linked to order `400`, unknown id `404` |

### Fulfillment Centers

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create FC | `POST` | `/data/fcs` | valid payload `200`, duplicate `fcId` `400`, missing coordinates `400`, no login `401` |
| List FCs | `GET` | `/data/fcs` | logged-in user `200`, no login `401` |
| Update FC | `PUT` | `/data/fcs/{id}` | valid update `200`, duplicate `fcId` `400`, unknown id `404` |
| Delete FC | `DELETE` | `/data/fcs/{id}` | unused FC `204`, FC linked to product `400`, unknown id `404` |

### Pickers

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create picker | `POST` | `/data/pickers` | super admin/admin create picker `200`, password missing `400`, associate `403`, duplicate email `400`, duplicate employee id `400`, no login `401` |
| List pickers | `GET` | `/data/pickers` | logged-in user `200`, no login `401` |
| Get picker | `GET` | `/data/pickers/{id}` | valid id `200`, unknown id `404`, no login `401` |
| Update picker | `PUT` | `/data/pickers/{id}` | admin update `200`, blank password keeps current password, associate `403`, duplicate email `400`, duplicate employee id `400`, unknown id `404` |
| Delete picker | `DELETE` | `/data/pickers/{id}` | admin delete unused picker `204`, delete assigned picker `400`, associate `403`, unknown id `404` |

### Products

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create product | `POST` | `/data/products` | valid client+FC `200`, duplicate name `400`, duplicate productId `400`, duplicate sku `400`, unknown client `404`, unknown FC `404`, negative price/invalid qty `400` |
| List products | `GET` | `/data/products` | logged-in user `200`, no login `401` |
| Inventory snapshot | `GET` | `/data/inventory-snapshot` | logged-in user `200`, verify sort/order and quantity values |
| Update product | `PUT` | `/data/products/{id}` | valid update `200`, duplicate identifiers `400`, unknown product `404`, unknown client/FC `404` |
| Delete product | `DELETE` | `/data/products/{id}` | valid delete `204`, unknown product `404` |

### Orders

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create order | `POST` | `/data/orders` | valid order `200`, item client mismatch `400`, missing items `400`, unknown product for client+FC `404`, unknown client `404`, unknown FC `404`, low inventory should create back-order response/state |
| List orders | `GET` | `/data/orders` | logged-in user `200`, verify active-batch exclusions if any |
| Back-order audits | `GET` | `/data/back-order-audits` | logged-in user `200`, verify audit history after back-order activity |
| Update order status | `PUT` | `/data/orders/{orderNumber}/status` | valid transitions `200`, invalid transition `400`, assigned order cannot move back or cancel `400`, trying `PACKING` or `PACKED` here `400`, unknown order `404` |
| Assign order to picker | `POST` | `/data/orders/{orderNumber}/assign/{pickerId}` | valid manager assign `200`, picker can only target itself `403`, unknown picker `404`, order not in `CREATED` `400`, already assigned `400`, active batch member `400` |
| Assign order to self | `POST` | `/data/orders/{orderNumber}/assign-self` | picker account `200`, logged-in user not registered as picker `403`, order not in `CREATED` `400` |
| Mark order picked | `POST` | `/data/orders/{orderNumber}/pick/{pickerId}` | manager-assisted pick `200`, picker targeting different picker `403`, order not in `ASSIGNED_FOR_PICKING` `400`, unknown picker `404` |
| Mark own order picked | `POST` | `/data/orders/{orderNumber}/pick-self` | assigned picker picks `200`, non-picker login `403`, different picker login `403`, order not in `ASSIGNED_FOR_PICKING` `400` |
| Pack order | `POST` | `/data/orders/{orderNumber}/pack` | picked order + positive weight `200`, non-picked order `400`, zero/negative weight `400` |
| Mark shipped | `POST` | `/data/orders/{orderNumber}/ship` | packed order `200`, non-packed order `400`, unknown order `404` |
| Mark delivered | `POST` | `/data/orders/{orderNumber}/deliver` | shipped order `200`, non-shipped order `400`, unknown order `404` |
| Cancel order | `POST` | `/data/orders/{orderNumber}/cancel` | created/shipped order `200`, delivered order `400`, repeat cancel should stay safe |
| Delete order | `DELETE` | `/data/orders/{orderNumber}` | cancelled or standalone order `204`, active batch member `400`, unknown order `404` |

### Batches

| API | Method | URL | Main Test Cases |
|---|---|---|---|
| Create batch | `POST` | `/data/batches` | valid created orders from same FC/warehouse `200`, duplicate order numbers `400`, created order from mixed FC/warehouse `400`, already assigned order `400`, back-order order `400`, active-batch order `400`, unknown order `404`, empty list `400` |
| List batches | `GET` | `/data/batches` | logged-in user `200`, no login `401` |
| Get batch | `GET` | `/data/batches/{batchId}` | valid id `200`, unknown id `404`, no login `401` |
| Update batch | `PUT` | `/data/batches/{batchId}` | update created batch `200`, update assigned/picked batch `400`, mixed FC/warehouse `400`, duplicate order numbers `400`, unknown batch `404` |
| Assign batch to picker | `POST` | `/data/batches/{batchId}/assign/{pickerId}` | valid manager assign `200`, picker can only target itself `403`, unknown picker `404`, non-created batch `400`, no login `401` |
| Assign batch to self | `POST` | `/data/batches/{batchId}/assign-self` | picker login `200`, no matching picker `403`, non-created batch `400` |
| Mark batch picked | `POST` | `/data/batches/{batchId}/pick-self` | assigned picker picks own batch `200`, different logged-in user `403`, batch not assigned `400`, any order not in `ASSIGNED_FOR_PICKING` `400` |
| Delete batch | `DELETE` | `/data/batches/{batchId}` | delete created batch `204`, delete assigned/picked batch `400`, unknown batch `404` |

## Important End-to-End Flows

### Flow A: Master Data Setup

1. `POST /data/login`
2. `POST /data/register`
3. `POST /data/pickers`
4. `POST /data/clients`
5. `POST /data/fcs`
6. `POST /data/products`
7. `GET /data/users`
8. `GET /data/pickers`
9. `GET /data/clients`
10. `GET /data/fcs`
11. `GET /data/products`

Expected result:

- All master records are created successfully.
- Save returned ids for later tests: `userId`, `pickerId`, `clientId`, `fcId`, `productId`.

### Flow B: Individual Order Lifecycle

1. Create order with `POST /data/orders`
2. List orders with `GET /data/orders`
3. Assign with `POST /data/orders/{orderNumber}/assign/{pickerId}`
4. Pick with `POST /data/orders/{orderNumber}/pick/{pickerId}`
5. Pack with `POST /data/orders/{orderNumber}/pack`
6. Ship with `POST /data/orders/{orderNumber}/ship`
7. Deliver with `POST /data/orders/{orderNumber}/deliver`

Expected result:

- Status progression should be:
  `CREATED -> ASSIGNED_FOR_PICKING -> PICKED -> PACKED -> SHIPPED -> DELIVERED`

### Flow C: Self-Assignment Flow

Precondition:

- Logged-in user email must exist in `pickers.email`.
- The matching login is now created from picker registration, so the picker should log in directly with its own password.

Steps:

1. Login as the associate user whose email matches a picker.
2. Create a fresh order or use a fresh created batch.
3. Call `POST /data/orders/{orderNumber}/assign-self`
4. Call `POST /data/batches/{batchId}/assign-self`
5. Call `POST /data/batches/{batchId}/pick-self`

Expected result:

- Self-assignment works only when the logged-in user is also a picker.
- Otherwise service returns `403`.

### Flow D: Back-Order Flow

1. Create product with low stock, for example quantity `1`.
2. Create order requesting quantity `5`.
3. Validate order is created with back-order behavior.
4. Call `GET /data/back-order-audits`
5. Replenish stock by updating the product quantity.
6. Try valid status recovery using `PUT /data/orders/{orderNumber}/status`

Expected result:

- The service should create or preserve back-order data.
- Invalid status changes should return `400`.

### Flow E: Batch Flow

1. Create two orders in `CREATED` status using the same `fcId` and `warehouseId`.
2. Call `POST /data/batches`
3. Verify with `GET /data/batches`
4. Assign with `POST /data/batches/{batchId}/assign/{pickerId}`
5. Login as matching picker user and call `POST /data/batches/{batchId}/pick-self`
6. Pack each order individually with `POST /data/orders/{orderNumber}/pack`
7. Verify batch status moves toward fulfilled

Expected result:

- Batch accepts only created, unassigned, same-FC/same-warehouse orders.
- Picking the batch marks all included orders as `PICKED`.

## Sample Request Bodies

### Register user

```json
{
  "name": "QA Admin",
  "email": "qa.admin.20260501@example.com",
  "password": "AdminPass123!",
  "role": "ADMIN"
}
```

### Create picker

```json
{
  "name": "QA Picker",
  "email": "qa.picker.20260501@example.com",
  "employeeId": "PK-20260501",
  "password": "PickerPass123!"
}
```

### Create client

```json
{
  "name": "Aarav Shah",
  "organisationName": "Northwind Retail 20260501",
  "organisationAddress": "221B Baker Street, London",
  "email": "ops.20260501@northwind.example",
  "phone": "+44-20-5555-0101"
}
```

### Create FC

```json
{
  "warehouseId": "WH-20260501",
  "fcId": "FC-20260501",
  "location": "Bengaluru South Hub",
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

### Create product

```json
{
  "name": "USB Barcode Scanner 20260501",
  "productId": "PRD-20260501",
  "sku": "SKU-20260501",
  "clientId": 1,
  "fcId": "FC-20260501",
  "description": "Handheld USB barcode scanner",
  "price": 2499.99,
  "quantity": 150
}
```

### Create order

```json
{
  "fcId": "FC-20260501",
  "clientId": 1,
  "billToAddress": "221B Baker Street, London",
  "shipToAddress": "7 Warehouse Avenue, Manchester",
  "items": [
    {
      "sku": "SKU-20260501",
      "productId": "PRD-20260501",
      "quantity": 3,
      "clientId": 1
    }
  ]
}
```

### Update order status

```json
{
  "status": "CANCELLED"
}
```

### Pack order

```json
{
  "weight": 18.75
}
```

### Create batch

```json
{
  "orderNumbers": [
    "ORDID-EXAMPLE-0001",
    "ORDID-EXAMPLE-0002"
  ]
}
```

## Fast Regression Checklist

- Login/logout works and session cookie is honored.
- Admin-only APIs reject associate users with `403`.
- Duplicate emails and duplicate IDs return `400`.
- Product creation requires valid client + FC.
- Order creation reduces product inventory.
- Low stock order produces back-order behavior.
- Order cannot be packed before pick.
- Order cannot ship before pack.
- Delivered order cannot be cancelled.
- Batch only accepts created orders from same FC + warehouse.
- Batch self-pick only works for the assigned picker.
- Deleting linked client/FC fails with `400`.

## Suggested Tools

- Swagger UI for manual testing
- Postman with cookie persistence enabled
- `curl` with a cookie jar:

```bash
curl -c cookies.txt -X POST http://localhost:8080/data/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"Admin@Example.com\",\"password\":\"Admin@123\"}"
```

```bash
curl -b cookies.txt http://localhost:8080/data/users
```
