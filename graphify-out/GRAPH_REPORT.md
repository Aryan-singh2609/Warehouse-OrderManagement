# Graph Report - new  (2026-05-04)

## Corpus Check
- 78 files · ~89,093 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 749 nodes · 1682 edges · 34 communities detected
- Extraction: 59% EXTRACTED · 41% INFERRED · 0% AMBIGUOUS · INFERRED: 685 edges (avg confidence: 0.8)
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
1. `OrderService` - 40 edges
2. `OrderInfo` - 29 edges
3. `BatchOrderInfo` - 26 edges
4. `OrderResponse` - 24 edges
5. `BatchOrderResponse` - 22 edges
6. `BatchService` - 20 edges
7. `ShippingLabelService` - 20 edges
8. `ProductResponse` - 18 edges
9. `ProductRequest` - 17 edges
10. `BatchInfo` - 16 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (5): BackOrderRepository, OrderItemInfo, Product, ProductRepository, ProductService

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (7): BatchController, ClientController, FcController, OrderController, PackingController, PackOrderRequest, ProductController

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (9): AuthController, DataInitializer, LoginResponse, NewApplication, PickerRepository, PickerService, User, UserRepository (+1 more)

### Community 3 - "Community 3"
Cohesion: 0.11
Nodes (6): BatchRequest, BatchServiceTest, ErrorResponse, FcInfoRepository, OrderSelfAssignmentTest, UpdateUserRequest

### Community 4 - "Community 4"
Cohesion: 0.05
Nodes (7): BackOrderAudit, BackOrderAuditRepository, BackOrderAuditScheduler, BackOrderFlowTest, OrderRequest, ProductRequest, ProductServiceTest

### Community 5 - "Community 5"
Cohesion: 0.11
Nodes (3): BatchOrderInfoRepository, OrderService, UserResponse

### Community 6 - "Community 6"
Cohesion: 0.06
Nodes (3): BatchInfo, BatchService, OrderInfoRepository

### Community 7 - "Community 7"
Cohesion: 0.1
Nodes (2): OrderInfo, ShippingLabelService

### Community 8 - "Community 8"
Cohesion: 0.08
Nodes (3): Client, ClientService, ProductResponse

### Community 9 - "Community 9"
Cohesion: 0.13
Nodes (1): OrderResponse

### Community 10 - "Community 10"
Cohesion: 0.16
Nodes (1): BatchOrderResponse

### Community 11 - "Community 11"
Cohesion: 0.14
Nodes (1): BatchOrderInfo

### Community 12 - "Community 12"
Cohesion: 0.15
Nodes (2): FcInfo, FcService

### Community 13 - "Community 13"
Cohesion: 0.11
Nodes (2): ApiExceptionHandler, BackOrder

### Community 14 - "Community 14"
Cohesion: 0.16
Nodes (3): PickerController, SessionControllerSupport, UserController

### Community 15 - "Community 15"
Cohesion: 0.23
Nodes (1): BatchResponse

### Community 16 - "Community 16"
Cohesion: 0.17
Nodes (1): ClientRequest

### Community 17 - "Community 17"
Cohesion: 0.17
Nodes (1): FcRequest

### Community 18 - "Community 18"
Cohesion: 0.29
Nodes (1): OrderItemResponse

### Community 19 - "Community 19"
Cohesion: 0.25
Nodes (1): PickerResponse

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
Nodes (1): PickerRequest

### Community 24 - "Community 24"
Cohesion: 0.2
Nodes (1): RegisterUserRequest

### Community 25 - "Community 25"
Cohesion: 0.43
Nodes (1): BackOrderAuditResponse

### Community 26 - "Community 26"
Cohesion: 0.29
Nodes (1): LoginRequest

### Community 27 - "Community 27"
Cohesion: 0.29
Nodes (1): Picker

### Community 28 - "Community 28"
Cohesion: 0.5
Nodes (1): OrderStatusUpdateRequest

### Community 29 - "Community 29"
Cohesion: 0.5
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
- **Thin community `Community 7`** (43 nodes): `OrderInfo.java`, `ShippingLabelService.java`, `OrderInfo`, `.attachShippingLabel()`, `.getBillToAddress()`, `.getBoxCategory()`, `.getBoxId()`, `.getClient()`, `.getCreatedAt()`, `.getFcLocation()`, `.getId()`, `.getOrderNumber()`, `.getPackedAt()`, `.getPackedWeight()`, `.getPickedAt()`, `.getPicker()`, `.getShippingLabelFileName()`, `.getShippingLabelGeneratedAt()`, `.getShipToAddress()`, `.markPacked()`, `.OrderInfo()`, `.prePersist()`, `.getClient()`, `ShippingLabelService`, `.buildDeliverToLines()`, `.buildShipFromLines()`, `.drawAddressSection()`, `.drawBorder()`, `.drawFooter()`, `.drawHeader()`, `.drawHorizontalDivider()`, `.drawItemsTable()`, `.drawOrderMetaSection()`, `.drawPaymentAndPackSection()`, `.drawSimpleBarcode()`, `.drawVerticalDivider()`, `.formatWeight()`, `.generateLabel()`, `.sanitize()`, `.splitAddressLines()`, `.truncate()`, `.writeCenteredText()`, `.writeText()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (25 nodes): `OrderResponse.java`, `.getShippingLabelPdf()`, `OrderResponse`, `.from()`, `.getBillToAddress()`, `.getBoxCategory()`, `.getBoxId()`, `.getClientId()`, `.getClientName()`, `.getCreatedAt()`, `.getFcId()`, `.getFcLocation()`, `.getItems()`, `.getOrderNumber()`, `.getPackedAt()`, `.getPackedWeight()`, `.getPickedAt()`, `.getPickerId()`, `.getShippingLabelDownloadUrl()`, `.getShippingLabelGeneratedAt()`, `.getShipToAddress()`, `.getStatus()`, `.getWarehouseId()`, `.isShippingLabelAvailable()`, `.OrderResponse()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (23 nodes): `BatchOrderResponse`, `.BatchOrderResponse()`, `.from()`, `.getBackOrderedQuantity()`, `.getBillToAddress()`, `.getClientId()`, `.getClientName()`, `.getFcId()`, `.getFcLocation()`, `.getFulfilledQuantity()`, `.getId()`, `.getOrderNumber()`, `.getOrderStatus()`, `.getPickerEmail()`, `.getPickerEmployeeId()`, `.getPickerId()`, `.getPickerName()`, `.getProductId()`, `.getQuantity()`, `.getShipToAddress()`, `.getSku()`, `.getWarehouseId()`, `BatchOrderResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (22 nodes): `BatchOrderInfo`, `.attachTo()`, `.BatchOrderInfo()`, `.getBatchInfo()`, `.getBillToAddress()`, `.getClientId()`, `.getClientName()`, `.getFcId()`, `.getFcLocation()`, `.getFulfilledQuantity()`, `.getId()`, `.getOrderItemInfoId()`, `.getOrderNumber()`, `.getPickerEmployeeId()`, `.getPickerId()`, `.getProductId()`, `.getQuantity()`, `.getShipToAddress()`, `.getSku()`, `.getWarehouseId()`, `.syncFrom()`, `BatchOrderInfo.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (21 nodes): `FcInfo`, `.FcInfo()`, `.getFcId()`, `.getId()`, `.getLatitude()`, `.getLocation()`, `.getLongitude()`, `.getProducts()`, `.getWarehouseId()`, `.update()`, `FcService`, `.createFc()`, `.deleteFc()`, `.FcService()`, `.getFcs()`, `.requireManager()`, `.updateFc()`, `.validateUniqueFcId()`, `FcInfo.java`, `FcService.java`, `.getWarehouseId()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (19 nodes): `ApiExceptionHandler`, `.handleResponseStatusException()`, `.handleValidationException()`, `BackOrder`, `.attachTo()`, `.BackOrder()`, `.getBackOrderedQuantity()`, `.getClientId()`, `.getCreatedAt()`, `.getId()`, `.getLastAuditedAt()`, `.getOrderInfo()`, `.getProductId()`, `.getReason()`, `.getSku()`, `.markAudited()`, `.prePersist()`, `ApiExceptionHandler.java`, `BackOrder.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (12 nodes): `BatchResponse`, `.BatchResponse()`, `.from()`, `.getCreatedAt()`, `.getOrderCount()`, `.getOrders()`, `.getPickerEmail()`, `.getPickerEmployeeId()`, `.getPickerId()`, `.getPickerName()`, `.getStatus()`, `BatchResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (12 nodes): `ClientRequest.java`, `ClientRequest`, `.getEmail()`, `.getName()`, `.getOrganisationAddress()`, `.getOrganisationName()`, `.getPhone()`, `.setEmail()`, `.setName()`, `.setOrganisationAddress()`, `.setOrganisationName()`, `.setPhone()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (12 nodes): `FcRequest`, `.getFcId()`, `.getLatitude()`, `.getLocation()`, `.getLongitude()`, `.getWarehouseId()`, `.setFcId()`, `.setLatitude()`, `.setLocation()`, `.setLongitude()`, `.setWarehouseId()`, `FcRequest.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (11 nodes): `OrderItemResponse.java`, `OrderItemResponse`, `.from()`, `.getBackOrderedQuantity()`, `.getClientId()`, `.getFulfilledQuantity()`, `.getOrderNumber()`, `.getProductId()`, `.getQuantity()`, `.getSku()`, `.OrderItemResponse()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 19`** (11 nodes): `PickerResponse.java`, `PickerResponse`, `.from()`, `.getEmail()`, `.getEmployeeId()`, `.getId()`, `.getName()`, `.PickerResponse()`, `.setEmail()`, `.setId()`, `.setName()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 20`** (10 nodes): `ClientResponse.java`, `ClientResponse`, `.ClientResponse()`, `.from()`, `.getEmail()`, `.getId()`, `.getName()`, `.getOrganisationAddress()`, `.getOrganisationName()`, `.getPhone()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 21`** (10 nodes): `FcResponse.java`, `FcResponse`, `.FcResponse()`, `.from()`, `.getFcId()`, `.getId()`, `.getLatitude()`, `.getLocation()`, `.getLongitude()`, `.getWarehouseId()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 22`** (10 nodes): `OrderItemRequest.java`, `OrderItemRequest`, `.getClientId()`, `.getProductId()`, `.getQuantity()`, `.getSku()`, `.setClientId()`, `.setProductId()`, `.setQuantity()`, `.setSku()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 23`** (10 nodes): `PickerRequest.java`, `PickerRequest`, `.getEmail()`, `.getEmployeeId()`, `.getName()`, `.getPassword()`, `.setEmail()`, `.setEmployeeId()`, `.setName()`, `.setPassword()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 24`** (10 nodes): `RegisterUserRequest.java`, `RegisterUserRequest`, `.getEmail()`, `.getName()`, `.getPassword()`, `.getRole()`, `.setEmail()`, `.setName()`, `.setPassword()`, `.setRole()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 25`** (7 nodes): `BackOrderAuditResponse`, `.BackOrderAuditResponse()`, `.from()`, `.getAuditedAt()`, `.getBackOrderCount()`, `.getId()`, `BackOrderAuditResponse.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 26`** (7 nodes): `LoginRequest.java`, `LoginRequest`, `.getEmail()`, `.getPassword()`, `.LoginRequest()`, `.setEmail()`, `.setPassword()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 27`** (7 nodes): `Picker.java`, `Picker`, `.getEmail()`, `.getId()`, `.getName()`, `.Picker()`, `.update()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 28`** (4 nodes): `OrderStatusUpdateRequest.java`, `OrderStatusUpdateRequest`, `.getStatus()`, `.setStatus()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 29`** (4 nodes): `BatchInfoRepository`, `.existsByPicker_Id()`, `.findAllByOrderByCreatedAtDesc()`, `BatchInfoRepository.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 30`** (3 nodes): `ClientRepository`, `.findByEmailIgnoreCase()`, `ClientRepository.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 31`** (3 nodes): `NewApplicationTests`, `.contextLoads()`, `NewApplicationTests.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 32`** (2 nodes): `OpenApiConfig.java`, `OpenApiConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 33`** (2 nodes): `OrderItemInfoRepository.java`, `OrderItemInfoRepository`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `OrderService` connect `Community 5` to `Community 0`, `Community 1`, `Community 2`, `Community 3`, `Community 6`?**
  _High betweenness centrality (0.037) - this node is a cross-community bridge._
- **Why does `OrderInfo` connect `Community 7` to `Community 0`, `Community 5`, `Community 6`, `Community 9`, `Community 12`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **What connects `OpenApiConfig`, `OrderItemInfoRepository` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.07 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.07 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.11 - nodes in this community are weakly interconnected._