package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.BackOrderAuditResponse;
import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.BackOrder;
import com.example.demo.entity.BatchOrderInfo;
import com.example.demo.entity.BatchStatus;
import com.example.demo.entity.BoxCategory;
import com.example.demo.entity.Client;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.OrderInfo;
import com.example.demo.entity.OrderItemInfo;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Picker;
import com.example.demo.entity.Product;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.BackOrderAuditRepository;
import com.example.demo.repository.BackOrderRepository;
import com.example.demo.repository.BatchInfoRepository;
import com.example.demo.repository.BatchOrderInfoRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.OrderInfoRepository;
import com.example.demo.repository.PickerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class OrderService {

    private static final String ORDER_ID_PREFIX = "ORDID";
    private static final List<BatchStatus> ACTIVE_BATCH_STATUSES = List.of(
            BatchStatus.CREATED,
            BatchStatus.ASSIGNED,
            BatchStatus.PICKED
    );
    private static final BigDecimal PARCEL_LIMIT = BigDecimal.valueOf(20);
    private static final BigDecimal SMALL_LIMIT = BigDecimal.valueOf(50);
    private static final BigDecimal MEDIUM_EXCLUSIVE_LIMIT = BigDecimal.valueOf(100);

    private final OrderInfoRepository orderInfoRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final FcInfoRepository fcInfoRepository;
    private final UserRepository userRepository;
    private final PickerRepository pickerRepository;
    private final BackOrderRepository backOrderRepository;
    private final BackOrderAuditRepository backOrderAuditRepository;
    private final BatchInfoRepository batchInfoRepository;
    private final BatchOrderInfoRepository batchOrderInfoRepository;
    private final ShippingLabelService shippingLabelService;

    public OrderService(
            OrderInfoRepository orderInfoRepository,
            ProductRepository productRepository,
            ClientRepository clientRepository,
            FcInfoRepository fcInfoRepository,
            UserRepository userRepository,
            PickerRepository pickerRepository,
            BackOrderRepository backOrderRepository,
            BackOrderAuditRepository backOrderAuditRepository,
            BatchInfoRepository batchInfoRepository,
            BatchOrderInfoRepository batchOrderInfoRepository,
            ShippingLabelService shippingLabelService
    ) {
        this.orderInfoRepository = orderInfoRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.fcInfoRepository = fcInfoRepository;
        this.userRepository = userRepository;
        this.pickerRepository = pickerRepository;
        this.backOrderRepository = backOrderRepository;
        this.backOrderAuditRepository = backOrderAuditRepository;
        this.batchInfoRepository = batchInfoRepository;
        this.batchOrderInfoRepository = batchOrderInfoRepository;
        this.shippingLabelService = shippingLabelService;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, long actorId) {
        requireOrderManager(actorId);
        String orderNumber = generateOrderNumber();

        Client client = getClient(request.getClientId());
        FcInfo fcInfo = getFc(request.getFcId());
        OrderStatus requestedStatus = request.getStatus() == null ? OrderStatus.CREATED : request.getStatus();

        OrderInfo orderInfo = new OrderInfo(
                orderNumber,
                fcInfo.getWarehouseId(),
                fcInfo.getFcId(),
                fcInfo.getLocation(),
                client,
                request.getBillToAddress(),
                request.getShipToAddress(),
                requestedStatus
        );

        FulfillmentDecision decision = fulfillOrderItems(request, orderInfo, orderNumber);
        decision.products().forEach(productRepository::save);
        decision.orderItems().forEach(orderInfo::addItem);

        if (!decision.hasAnyFulfilledQuantity()) {
            orderInfo.updateStatus(OrderStatus.BACK_ORDER);
        }

        OrderInfo savedOrder = orderInfoRepository.save(orderInfo);
        if (!decision.backOrders().isEmpty()) {
            decision.backOrders().forEach(backOrder -> backOrder.attachTo(savedOrder));
            backOrderRepository.saveAll(decision.backOrders());
        }

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(long actorId) {
        User actor = requireLogin(actorId);
        if (actor.getRole() == Role.PICKER) {
            return orderInfoRepository.findAllAvailableForPickerQueue(ACTIVE_BATCH_STATUSES)
                    .stream()
                    .map(OrderResponse::from)
                    .toList();
        }

        requireOrderManager(actorId);
        return orderInfoRepository.findAllAvailableForIndividualOperations(ACTIVE_BATCH_STATUSES)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getPickerOrderHistory(long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = resolvePickerForActor(actor);
        return orderInfoRepository.findAllByPicker_IdOrderByCreatedAtDesc(picker.getId())
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BackOrderAuditResponse> getBackOrderAudits(long actorId) {
        requireOrderManager(actorId);
        return backOrderAuditRepository.findAllByOrderByAuditedAtDesc()
                .stream()
                .map(BackOrderAuditResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderNumber, OrderStatus status, long actorId) {
        requireOrderManager(actorId);
        if (status == OrderStatus.PACKED || status == OrderStatus.PACKING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use the packing API to transition an order to PACKED"
            );
        }

        OrderInfo orderInfo = getOrder(orderNumber);
        if (orderInfo.getStatus() == status) {
            return OrderResponse.from(orderInfo);
        }

        validateStatusTransition(orderInfo.getStatus(), status);

        if (orderInfo.getStatus() == OrderStatus.BACK_ORDER && status == OrderStatus.CREATED) {
            reserveBackOrderedItems(orderInfo);
            backOrderRepository.deleteByOrderInfo(orderInfo);
        } else if (orderInfo.getStatus() != OrderStatus.CANCELLED && status == OrderStatus.CANCELLED) {
            restoreProducts(orderInfo);
            backOrderRepository.deleteByOrderInfo(orderInfo);
        } else if (orderInfo.getStatus() == OrderStatus.CANCELLED && status != OrderStatus.CANCELLED) {
            reserveFulfilledItems(orderInfo);
        }

        if (orderInfo.getStatus() == OrderStatus.ASSIGNED_FOR_PICKING && status == OrderStatus.PICKED) {
            if (orderInfo.getPicker() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned order must have a picker before it can be marked as picked");
            }
            orderInfo.markPicked();
        } else {
            orderInfo.updateStatus(status);
        }

        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional
    public OrderResponse assignOrder(String orderNumber, long pickerId, long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = pickerRepository.findById(pickerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));

        if (actor.getRole() == Role.PICKER && !picker.getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pickers can only assign orders to themselves");
        }

        OrderInfo orderInfo = getOrder(orderNumber);
        ensureNotInActiveBatch(orderInfo);
        requireAssignable(orderInfo, "assigned");

        orderInfo.assignPicker(picker);
        return OrderResponse.from(orderInfoRepository.save(orderInfo));
    }

    @Transactional
    public OrderResponse assignOrderToSelf(String orderNumber, long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = resolvePickerForActor(actor);

        OrderInfo orderInfo = getOrder(orderNumber);
        ensureNotInActiveBatch(orderInfo);
        requireAssignable(orderInfo, "self-assigned");

        orderInfo.assignPicker(picker);
        return OrderResponse.from(orderInfoRepository.save(orderInfo));
    }

    @Transactional
    public OrderResponse markOrderPicked(String orderNumber, long pickerId, long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = pickerRepository.findById(pickerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));

        if (actor.getRole() == Role.PICKER && !picker.getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pickers can only mark their own orders as picked");
        }

        OrderInfo orderInfo = getOrder(orderNumber);
        if (orderInfo.getStatus() != OrderStatus.ASSIGNED_FOR_PICKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must be in ASSIGNED_FOR_PICKING status to be marked as picked");
        }

        if (orderInfo.getPicker() == null || orderInfo.getPicker().getId() != pickerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned picker can mark the order as picked");
        }

        orderInfo.markPicked();
        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional
    public OrderResponse markOrderPickedByActor(String orderNumber, long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = resolvePickerForActor(actor);
        return markOrderPicked(orderNumber, picker.getId(), actorId);
    }

    @Transactional
    public OrderResponse markOrderShipped(String orderNumber, long actorId) {
        requireOrderManager(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);

        if (orderInfo.getStatus() != OrderStatus.PACKED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must be in PACKED status to be marked as shipped");
        }

        orderInfo.updateStatus(OrderStatus.SHIPPED);
        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional
    public OrderResponse packOrder(String orderNumber, BigDecimal weight, long actorId) {
        requireOrderManager(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);

        if (orderInfo.getStatus() != OrderStatus.PICKED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must be in PICKED status to be packed");
        }

        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weight must be greater than 0");
        }

        BoxCategory boxCategory = determineBoxCategory(weight);
        String boxId = generateBoxId(boxCategory);
        orderInfo.markPacked(boxCategory, boxId, weight);
        ShippingLabelService.ShippingLabelDocument shippingLabel = shippingLabelService.generateLabel(orderInfo);
        orderInfo.attachShippingLabel(shippingLabel.fileName(), shippingLabel.content());

        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional(readOnly = true)
    public ShippingLabelService.ShippingLabelDocument getShippingLabel(String orderNumber, long actorId) {
        User actor = requireLogin(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);

        if (actor.getRole() == Role.PICKER) {
            Picker picker = resolvePickerForActor(actor);
            if (orderInfo.getPicker() == null || orderInfo.getPicker().getId() != picker.getId()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pickers can only access labels for their own orders");
            }
        } else {
            requireOrderManager(actorId);
        }

        if (orderInfo.getShippingLabelPdf() == null || orderInfo.getShippingLabelPdf().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping label has not been generated for this order yet");
        }

        return new ShippingLabelService.ShippingLabelDocument(
                orderInfo.getShippingLabelFileName(),
                orderInfo.getShippingLabelPdf()
        );
    }

    @Transactional
    public OrderResponse markOrderDelivered(String orderNumber, long actorId) {
        requireOrderManager(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);

        if (orderInfo.getStatus() != OrderStatus.SHIPPED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must be in SHIPPED status to be marked as delivered");
        }

        orderInfo.updateStatus(OrderStatus.DELIVERED);
        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional
    public OrderResponse markOrderCancelled(String orderNumber, long actorId) {
        requireOrderManager(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);
        if (orderInfo.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivered orders cannot be cancelled");
        }

        if (orderInfo.getStatus() == OrderStatus.CANCELLED) {
            return OrderResponse.from(orderInfo);
        }

        if (orderInfo.getStatus() == OrderStatus.ASSIGNED_FOR_PICKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned orders must be picked and cannot be cancelled");
        }

        restoreProducts(orderInfo);
        backOrderRepository.deleteByOrderInfo(orderInfo);
        orderInfo.updateStatus(OrderStatus.CANCELLED);
        OrderResponse response = OrderResponse.from(orderInfoRepository.save(orderInfo));
        syncActiveBatchState(orderInfo);
        return response;
    }

    @Transactional
    public void deleteOrder(String orderNumber, long actorId) {
        requireOrderManager(actorId);
        OrderInfo orderInfo = getOrder(orderNumber);
        ensureNotInActiveBatch(orderInfo);

        if (orderInfo.getStatus() == OrderStatus.ASSIGNED_FOR_PICKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned orders cannot be deleted");
        }

        backOrderRepository.deleteByOrderInfo(orderInfo);
        if (orderInfo.getStatus() != OrderStatus.CANCELLED) {
            restoreProducts(orderInfo);
        }

        orderInfoRepository.delete(orderInfo);
    }

    private FulfillmentDecision fulfillOrderItems(OrderRequest request, OrderInfo orderInfo, String orderNumber) {
        List<Product> products = new ArrayList<>();
        List<OrderItemInfo> orderItems = new ArrayList<>();
        List<BackOrder> backOrders = new ArrayList<>();
        boolean hasAnyFulfilledQuantity = false;

        for (OrderItemRequest item : request.getItems()) {
            validateItemClient(request.getClientId(), item);
            Product product = getProduct(orderInfo.getFcId(), item);

            int fulfilledQuantity = Math.min(product.getQuantity(), item.getQuantity());
            int backOrderedQuantity = item.getQuantity() - fulfilledQuantity;

            product.update(
                    product.getName(),
                    product.getProductId(),
                    product.getSku(),
                    product.getClient(),
                    product.getFcInfo(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity() - fulfilledQuantity
            );
            products.add(product);

            orderItems.add(new OrderItemInfo(
                    orderNumber,
                    item.getSku(),
                    item.getProductId(),
                    item.getQuantity(),
                    fulfilledQuantity,
                    item.getClientId()
            ));

            if (fulfilledQuantity > 0) {
                hasAnyFulfilledQuantity = true;
            }

            if (backOrderedQuantity > 0) {
                backOrders.add(new BackOrder(
                        orderInfo,
                        item.getSku(),
                        item.getProductId(),
                        item.getClientId(),
                        backOrderedQuantity,
                        "Insufficient quantity for product " + product.getName()
                ));
            }
        }

        return new FulfillmentDecision(products, orderItems, backOrders, hasAnyFulfilledQuantity);
    }

    private void reserveBackOrderedItems(OrderInfo orderInfo) {
        List<BackOrder> backOrders = backOrderRepository.findAllByOrderInfo(orderInfo);
        for (BackOrder backOrder : backOrders) {
            Product product = productRepository
                    .findByProductIdIgnoreCaseAndSkuIgnoreCaseAndClient_IdAndFcInfo_FcIdIgnoreCase(
                            backOrder.getProductId(),
                            backOrder.getSku(),
                            backOrder.getClientId(),
                            orderInfo.getFcId()
                    )
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for back order"));

            if (product.getQuantity() < backOrder.getBackOrderedQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient quantity for product " + product.getName()
                );
            }

            product.update(
                    product.getName(),
                    product.getProductId(),
                    product.getSku(),
                    product.getClient(),
                    product.getFcInfo(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity() - backOrder.getBackOrderedQuantity()
            );
            productRepository.save(product);

            findMatchingOrderItem(orderInfo, backOrder)
                    .increaseFulfilledQuantity(backOrder.getBackOrderedQuantity());
        }
    }

    private void reserveFulfilledItems(OrderInfo orderInfo) {
        for (OrderItemInfo item : orderInfo.getItems()) {
            if (item.getFulfilledQuantity() == 0) {
                continue;
            }

            Product product = productRepository
                    .findByProductIdIgnoreCaseAndSkuIgnoreCaseAndClient_IdAndFcInfo_FcIdIgnoreCase(
                            item.getProductId(),
                            item.getSku(),
                            item.getClientId(),
                            orderInfo.getFcId()
                    )
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for the selected client"));

            if (product.getQuantity() < item.getFulfilledQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient quantity for product " + product.getName());
            }

            product.update(
                    product.getName(),
                    product.getProductId(),
                    product.getSku(),
                    product.getClient(),
                    product.getFcInfo(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity() - item.getFulfilledQuantity()
            );
            productRepository.save(product);
        }
    }

    private void restoreProducts(OrderInfo orderInfo) {
        for (OrderItemInfo item : orderInfo.getItems()) {
            if (item.getFulfilledQuantity() == 0) {
                continue;
            }

            productRepository.findByProductIdIgnoreCaseAndSkuIgnoreCaseAndClient_IdAndFcInfo_FcIdIgnoreCase(
                            item.getProductId(),
                            item.getSku(),
                            item.getClientId(),
                            orderInfo.getFcId()
                    )
                    .ifPresent(product -> {
                        product.update(
                                product.getName(),
                                product.getProductId(),
                                product.getSku(),
                                product.getClient(),
                                product.getFcInfo(),
                                product.getDescription(),
                                product.getPrice(),
                                product.getQuantity() + item.getFulfilledQuantity()
                        );
                        productRepository.save(product);
                    });
        }
    }

    private void validateItemClient(long clientId, OrderItemRequest item) {
        if (item.getClientId() == null || item.getClientId() != clientId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All order items must belong to the selected client");
        }
    }

    private Product getProduct(String fcId, OrderItemRequest item) {
        return productRepository
                .findByProductIdIgnoreCaseAndSkuIgnoreCaseAndClient_IdAndFcInfo_FcIdIgnoreCase(
                        item.getProductId(),
                        item.getSku(),
                        item.getClientId(),
                        fcId
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for the selected client and FC"));
    }

    private OrderItemInfo findMatchingOrderItem(OrderInfo orderInfo, BackOrder backOrder) {
        return orderInfo.getItems().stream()
                .filter(item -> item.getProductId().equalsIgnoreCase(backOrder.getProductId())
                        && item.getSku().equalsIgnoreCase(backOrder.getSku())
                        && item.getClientId() == backOrder.getClientId())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found for back order"));
    }

    private void requireAssignable(OrderInfo orderInfo, String action) {
        if (orderInfo.getStatus() != OrderStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must be in CREATED status to be " + action);
        }

        if (orderInfo.getPicker() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already assigned to a picker");
        }
    }

    private void ensureNotInActiveBatch(OrderInfo orderInfo) {
        if (batchOrderInfoRepository.existsByOrderInfoAndBatchInfo_StatusIn(
                orderInfo,
                ACTIVE_BATCH_STATUSES
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order is part of an active batch and cannot be handled individually"
            );
        }
    }

    private void syncActiveBatchState(OrderInfo orderInfo) {
        List<BatchOrderInfo> batchOrderInfos = batchOrderInfoRepository.findAllByOrderInfo(orderInfo);
        if (batchOrderInfos.isEmpty()) {
            return;
        }

        List<com.example.demo.entity.BatchInfo> batchesToSave = new ArrayList<>();
        for (BatchOrderInfo batchOrderInfo : batchOrderInfos) {
            com.example.demo.entity.BatchInfo batchInfo = batchOrderInfo.getBatchInfo();
            if (batchInfo.getStatus() == BatchStatus.FULFILLED) {
                continue;
            }

            OrderItemInfo item = orderInfo.getItems().stream()
                    .filter(orderItemInfo -> orderItemInfo.getId() == batchOrderInfo.getOrderItemInfoId())
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch item could not be synchronized"));
            batchOrderInfo.syncFrom(orderInfo, item);

            BatchStatus nextStatus = resolveBatchStatus(batchInfo);
            if (nextStatus == BatchStatus.FULFILLED) {
                batchInfo.markFulfilled();
            } else {
                batchInfo.updateStatus(nextStatus);
            }
            batchesToSave.add(batchInfo);
        }

        batchInfoRepository.saveAll(batchesToSave);
    }

    private BatchStatus resolveBatchStatus(com.example.demo.entity.BatchInfo batchInfo) {
        if (batchInfo.getPicker() == null) {
            return BatchStatus.CREATED;
        }

        List<OrderStatus> orderStatuses = batchInfo.getOrders().stream()
                .map(BatchOrderInfo::getOrderInfo)
                .distinct()
                .map(OrderInfo::getStatus)
                .toList();

        if (orderStatuses.stream().anyMatch(status -> status == OrderStatus.ASSIGNED_FOR_PICKING
                || status == OrderStatus.CREATED
                || status == OrderStatus.BACK_ORDER)) {
            return BatchStatus.ASSIGNED;
        }

        if (orderStatuses.stream().allMatch(this::isFulfilledForBatch)) {
            return BatchStatus.FULFILLED;
        }

        if (orderStatuses.stream().anyMatch(status -> status == OrderStatus.PICKED
                || status == OrderStatus.PACKED
                || status == OrderStatus.PACKING)) {
            return BatchStatus.PICKED;
        }

        return BatchStatus.ASSIGNED;
    }

    private boolean isFulfilledForBatch(OrderStatus status) {
        return status == OrderStatus.PACKED
                || status == OrderStatus.SHIPPED
                || status == OrderStatus.DELIVERED;
    }

    private OrderInfo getOrder(String orderNumber) {
        return orderInfoRepository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        if (currentStatus == OrderStatus.DELIVERED && nextStatus != OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivered orders cannot change status");
        }

        if (currentStatus == OrderStatus.CANCELLED && nextStatus != OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancelled orders cannot change status");
        }

        if (!allowedTransitionsFrom(currentStatus).contains(nextStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order status cannot change from " + currentStatus + " to " + nextStatus
            );
        }
    }

    private EnumSet<OrderStatus> allowedTransitionsFrom(OrderStatus status) {
        return switch (status) {
            case CREATED -> EnumSet.of(OrderStatus.ASSIGNED_FOR_PICKING, OrderStatus.CANCELLED);
            case BACK_ORDER -> EnumSet.of(OrderStatus.CREATED, OrderStatus.CANCELLED);
            case ASSIGNED_FOR_PICKING -> EnumSet.of(OrderStatus.PICKED);
            case PICKED -> EnumSet.of(OrderStatus.PACKED, OrderStatus.CANCELLED);
            case PACKING -> EnumSet.of(OrderStatus.PACKED, OrderStatus.CANCELLED);
            case PACKED -> EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED);
            case SHIPPED -> EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);
            case DELIVERED -> EnumSet.of(OrderStatus.DELIVERED);
            case CANCELLED -> EnumSet.of(OrderStatus.CANCELLED);
        };
    }

    private BoxCategory determineBoxCategory(BigDecimal weight) {
        if (weight.compareTo(PARCEL_LIMIT) <= 0) {
            return BoxCategory.PARCEL_BOX;
        }

        if (weight.compareTo(SMALL_LIMIT) <= 0) {
            return BoxCategory.SMALL_BOX;
        }

        if (weight.compareTo(MEDIUM_EXCLUSIVE_LIMIT) < 0) {
            return BoxCategory.MEDIUM_BOX;
        }

        return BoxCategory.LARGE_BOX;
    }

    private String generateBoxId(BoxCategory boxCategory) {
        String categoryToken = boxCategory.name()
                .replace("_BOX", "")
                .replace('_', '-')
                .toUpperCase(Locale.ROOT);
        String uniqueToken = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        return "BOX-" + categoryToken + "-" + uniqueToken;
    }

    private String generateOrderNumber() {
        String orderNumber;
        do {
            String uniqueToken = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
            orderNumber = ORDER_ID_PREFIX + "-" + uniqueToken;
        } while (orderInfoRepository.findByOrderNumberIgnoreCase(orderNumber).isPresent());
        return orderNumber;
    }

    private Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    private FcInfo getFc(String fcId) {
        return fcInfoRepository.findByFcIdIgnoreCase(fcId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FC not found"));
    }

    private User requireOrderManager(long actorId) {
        User actor = requireLogin(actorId);

        if (actor.getRole() != Role.ASSOCIATE && actor.getRole() != Role.ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order management is not allowed");
        }

        return actor;
    }

    private User requireLogin(long actorId) {
        return userRepository.findById(actorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required"));
    }

    private Picker resolvePickerForActor(User actor) {
        return pickerRepository.findByEmailIgnoreCase(actor.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Logged in user is not registered as a picker"
                ));
    }

    private record FulfillmentDecision(
            List<Product> products,
            List<OrderItemInfo> orderItems,
            List<BackOrder> backOrders,
            boolean hasAnyFulfilledQuantity
    ) {
    }
}
