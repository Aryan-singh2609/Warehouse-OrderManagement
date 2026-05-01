# Graph Report - new  (2026-04-30)

## Corpus Check
- 76 files · ~74,151 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 677 nodes · 1293 edges · 34 communities detected
- Extraction: 64% EXTRACTED · 36% INFERRED · 0% AMBIGUOUS · INFERRED: 460 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]

## God Nodes (most connected - your core abstractions)
1. `OrderService` - 34 edges
2. `OrderInfo` - 26 edges
3. `BatchOrderInfo` - 22 edges
4. `OrderResponse` - 21 edges
5. `BatchService` - 20 edges
6. `BatchOrderResponse` - 18 edges
7. `ProductResponse` - 16 edges
8. `BatchInfo` - 16 edges
9. `ProductRequest` - 15 edges
10. `BackOrder` - 14 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.05
Nodes (7): Client, ClientService, ProductResponse, UpdateUserRequest, User, UserRepository, UserService

### Community 1 - "Community 1"
Cohesion: 0.08
Nodes (6): DataInitializer, NewApplication, OrderItemInfo, Product, ProductRepository, ProductService

### Community 2 - "Community 2"
Cohesion: 0.05
Nodes (7): BatchController, ClientController, FcController, OrderController, PackingController, PackOrderRequest, ProductController

### Community 3 - "Community 3"
Cohesion: 0.06
Nodes (4): BatchInfo, BatchOrderInfoRepository, BatchService, OrderInfoRepository

### Community 4 - "Community 4"
Cohesion: 0.14
Nodes (5): BatchRequest, BatchServiceTest, ErrorResponse, FcInfoRepository, PickerRepository

### Community 5 - "Community 5"
Cohesion: 0.07
Nodes (3): FcInfo, FcService, OrderInfo

### Community 6 - "Community 6"
Cohesion: 0.15
Nodes (2): OrderService, UserResponse

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (3): OrderRequest, OrderSelfAssignmentTest, ProductRequest

### Community 8 - "Community 8"
Cohesion: 0.09
Nodes (5): BackOrderAudit, BackOrderAuditRepository, BackOrderAuditScheduler, BackOrderFlowTest, BackOrderRepository

### Community 9 - "Community 9"
Cohesion: 0.16
Nodes (1): OrderResponse

### Community 10 - "Community 10"
Cohesion: 0.15
Nodes (1): BatchOrderInfo

### Community 11 - "Community 11"
Cohesion: 0.12
Nodes (2): BatchResponse, Picker

### Community 12 - "Community 12"
Cohesion: 0.11
Nodes (2): ApiExceptionHandler, BackOrder

### Community 13 - "Community 13"
Cohesion: 0.14
Nodes (3): PickerController, SessionControllerSupport, UserController

### Community 14 - "Community 14"
Cohesion: 0.19
Nodes (1): BatchOrderResponse

### Community 15 - "Community 15"
Cohesion: 0.17
Nodes (1): ClientRequest

### Community 16 - "Community 16"
Cohesion: 0.17
Nodes (1): FcRequest

### Community 17 - "Community 17"
Cohesion: 0.29
Nodes (1): OrderItemResponse

### Community 18 - "Community 18"
Cohesion: 0.25
Nodes (1): PickerResponse

### Community 19 - "Community 19"
Cohesion: 0.2
Nodes (2): AuthController, LoginResponse

### Community 20 - "Community 20"
Cohesion: 0.33
Nodes (1): ClientResponse

### Community 21 - "Community 21"
Cohesion: 0.33
Nodes (1): FcResponse

### Community 22 - "Community 22"
Cohesion: 0.2
Nodes (1): OrderItemRequest

### Community 23 - "Community 23"
Cohesion: 0.2
Nodes (1): RegisterUserRequest

### Community 24 - "Community 24"
Cohesion: 0.25
Nodes (1): PickerRequest

### Community 25 - "Community 25"
Cohesion: 0.43
Nodes (1): BackOrderAuditResponse

### Community 26 - "Community 26"
Cohesion: 0.29
Nodes (1): LoginRequest

### Community 27 - "Community 27"
Cohesion: 0.33
Nodes (1): PickerService

### Community 28 - "Community 28"
Cohesion: 0.5
Nodes (1): OrderStatusUpdateRequest

### Community 29 - "Community 29"
Cohesion: 0.67
Nodes (1): BatchInfoRepository

### Community 30 - "Community 30"
Cohesion: 0.67
Nodes (1): ClientRepository

### Community 31 - "Community 31"
Cohesion: 0.67
Nodes (1): NewApplicationTests

### Community 32 - "Community 32"
Cohesion: 1.0
Nodes (1): OpenApiConfig

### Community 33 - "Community 33"
Cohesion: 1.0
Nodes (1): OrderItemInfoRepository

## Knowledge Gaps
- **2 isolated node(s):** `OpenApiConfig`, `OrderItemInfoRepository`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 6`** (36 nodes): `.deleteByOrderInfo()`, `UserResponse.java`, `OrderService.java`, `.getStatus()`, `.markPicked()`, `.updateStatus()`, `OrderService`, `.allowedTransitionsFrom()`, `.assignOrder()`, `.assignOrderToSelf()`, `.deleteOrder()`, `.determineBoxCategory()`, `.ensureNotInActiveBatch()`, `.generateBoxId()`, `.getBackOrderAudits()`, `.getOrder()`, `.markOrderCancelled()`, `.markOrderDelivered()`, `.markOrderPicked()`, `.markOrderShipped()`, `.OrderService()`, `.packOrder()`, `.requireAssignable()`, `.requireOrderManager()`, `.resolveBatchStatus()`, `.syncActiveBatchState()`, `.updateOrderStatus()`, `.validateStatusTransition()`, `.getPicker()`, `UserResponse`, `.from()`, `.getEmail()`, `.getId()`, `.getName()`, `.getRole()`, `.UserResponse()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (21 nodes): `OrderResponse.java`, `OrderResponse`, `.from()`, `.getBillToAddress()`, `.getBoxCategory()`, `.getBoxId()`, `.getClientId()`, `.getClientName()`, `.getCreatedAt()`, `.getFcId()`, `.getFcLocation()`, `.getItems()`, `.getOrderNumber()`, `.getPackedAt()`, `.getPackedWeight()`, `.getPickedAt()`, `.getPickerId()`, `.getShipToAddress()`, `.getStatus()`, `.getWarehouseId()`, `.OrderResponse()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (21 nodes): `BatchOrderInfo`, `.attachTo()`, `.BatchOrderInfo()`, `.getBatchInfo()`, `.getBillToAddress()`, `.getClientId()`, `.getClientName()`, `.getFcId()`, `.getFcLocation()`, `.getFulfilledQuantity()`, `.getId()`, `.getOrderItemInfoId()`, `.getOrderNumber()`, `.getOrderStatus()`, `.getProductId()`, `.getQuantity()`, `.getShipToAddress()`, `.getSku()`, `.getWarehouseId()`, `.syncFrom()`, `BatchOrderInfo.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (20 nodes): `BatchResponse`, `.BatchResponse()`, `.from()`, `.getCreatedAt()`, `.getOrderCount()`, `.getOrders()`, `.getPickerEmail()`, `.getPickerEmployeeId()`, `.getPickerId()`, `.getPickerName()`, `.getStatus()`, `Picker.java`, `BatchResponse.java`, `Picker`, `.getEmail()`, `.getEmployeeId()`, `.getId()`, `.getName()`, `.Picker()`, `.update()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (19 nodes): `ApiExceptionHandler`, `.handleResponseStatusException()`, `.handleValidationException()`, `BackOrder`, `.attachTo()`, `.BackOrder()`, `.getBackOrderedQuantity()`, `.getClientId()`, `.getCreatedAt()`, `.getId()`, `.getLastAuditedAt()`, `.getOrderInfo()`, `.getProductId()`, `.getReason()`, `.getSku()`, `.markAudited()`, `.prePersist()`, `ApiExceptionHandler.java`, `BackOrder.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (19 nodes): `BatchOrderResponse`, `.BatchOrderResponse()`, `.from()`, `.getBackOrderedQuantity()`, `.getBillToAddress()`, `.getClientId()`, `.getClientName()`, `.getFcId()`, `.getFcLocation()`, `.getFulfilledQuantity()`, `.getId()`, `.getOrderNumber()`, `.getOrderStatus()`, `.getProductId()`, `.getQuantity()`, `.getShipToAddress()`, `.getSku()`, `.getWarehouseId()`, `BatchOrderResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (12 nodes): `ClientRequest`, `.getEmail()`, `.getName()`, `.getOrganisationAddress()`, `.getOrganisationName()`, `.getPhone()`, `.setEmail()`, `.setName()`, `.setOrganisationAddress()`, `.setOrganisationName()`, `.setPhone()`, `ClientRequest.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (12 nodes): `FcRequest`, `.getFcId()`, `.getLatitude()`, `.getLocation()`, `.getLongitude()`, `.getWarehouseId()`, `.setFcId()`, `.setLatitude()`, `.setLocation()`, `.setLongitude()`, `.setWarehouseId()`, `FcRequest.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (11 nodes): `OrderItemResponse.java`, `OrderItemResponse`, `.from()`, `.getBackOrderedQuantity()`, `.getClientId()`, `.getFulfilledQuantity()`, `.getOrderNumber()`, `.getProductId()`, `.getQuantity()`, `.getSku()`, `.OrderItemResponse()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (11 nodes): `PickerResponse.java`, `PickerResponse`, `.from()`, `.getEmail()`, `.getEmployeeId()`, `.getId()`, `.getName()`, `.PickerResponse()`, `.setEmail()`, `.setId()`, `.setName()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 19`** (10 nodes): `AuthController`, `.AuthController()`, `.login()`, `.logout()`, `LoginResponse`, `.getUser()`, `.getUsers()`, `.LoginResponse()`, `AuthController.java`, `LoginResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 20`** (10 nodes): `ClientResponse`, `.ClientResponse()`, `.from()`, `.getEmail()`, `.getId()`, `.getName()`, `.getOrganisationAddress()`, `.getOrganisationName()`, `.getPhone()`, `ClientResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 21`** (10 nodes): `FcResponse`, `.FcResponse()`, `.from()`, `.getFcId()`, `.getId()`, `.getLatitude()`, `.getLocation()`, `.getLongitude()`, `.getWarehouseId()`, `FcResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 22`** (10 nodes): `OrderItemRequest.java`, `OrderItemRequest`, `.getClientId()`, `.getProductId()`, `.getQuantity()`, `.getSku()`, `.setClientId()`, `.setProductId()`, `.setQuantity()`, `.setSku()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 23`** (10 nodes): `RegisterUserRequest.java`, `RegisterUserRequest`, `.getEmail()`, `.getName()`, `.getPassword()`, `.getRole()`, `.setEmail()`, `.setName()`, `.setPassword()`, `.setRole()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 24`** (8 nodes): `PickerRequest.java`, `PickerRequest`, `.getEmail()`, `.getEmployeeId()`, `.getName()`, `.setEmail()`, `.setEmployeeId()`, `.setName()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 25`** (7 nodes): `BackOrderAuditResponse`, `.BackOrderAuditResponse()`, `.from()`, `.getAuditedAt()`, `.getBackOrderCount()`, `.getId()`, `BackOrderAuditResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 26`** (7 nodes): `LoginRequest`, `.getEmail()`, `.getPassword()`, `.LoginRequest()`, `.setEmail()`, `.setPassword()`, `LoginRequest.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 27`** (7 nodes): `PickerService.java`, `PickerService`, `.deletePicker()`, `.findByEmail()`, `.findPicker()`, `.getPickers()`, `.PickerService()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 28`** (4 nodes): `OrderStatusUpdateRequest.java`, `OrderStatusUpdateRequest`, `.getStatus()`, `.setStatus()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 29`** (3 nodes): `BatchInfoRepository`, `.findAllByOrderByCreatedAtDesc()`, `BatchInfoRepository.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 30`** (3 nodes): `ClientRepository.java`, `ClientRepository`, `.findByEmailIgnoreCase()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 31`** (3 nodes): `NewApplicationTests.java`, `NewApplicationTests`, `.contextLoads()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 32`** (2 nodes): `OpenApiConfig.java`, `OpenApiConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 33`** (2 nodes): `OrderItemInfoRepository.java`, `OrderItemInfoRepository`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `OrderInfo` connect `Community 5` to `Community 1`, `Community 3`, `Community 6`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Why does `BatchInfo` connect `Community 3` to `Community 4`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **What connects `OpenApiConfig`, `OrderItemInfoRepository` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.05 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.05 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.06 - nodes in this community are weakly interconnected._