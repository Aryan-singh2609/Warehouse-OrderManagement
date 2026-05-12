package com.example.demo.service;

import com.example.demo.dto.BatchRequest;
import com.example.demo.dto.BatchResponse;
import com.example.demo.entity.BatchInfo;
import com.example.demo.entity.BatchOrderInfo;
import com.example.demo.entity.BatchStatus;
import com.example.demo.entity.OrderInfo;
import com.example.demo.entity.OrderItemInfo;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Picker;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.BatchInfoRepository;
import com.example.demo.repository.BatchOrderInfoRepository;
import com.example.demo.repository.OrderInfoRepository;
import com.example.demo.repository.PickerRepository;
import com.example.demo.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BatchService {

    private static final List<BatchStatus> ACTIVE_BATCH_STATUSES = List.of(
            BatchStatus.CREATED,
            BatchStatus.ASSIGNED,
            BatchStatus.PICKED
    );

    private final BatchInfoRepository batchInfoRepository;
    private final BatchOrderInfoRepository batchOrderInfoRepository;
    private final OrderInfoRepository orderInfoRepository;
    private final UserRepository userRepository;
    private final PickerRepository pickerRepository;

    public BatchService(
            BatchInfoRepository batchInfoRepository,
            BatchOrderInfoRepository batchOrderInfoRepository,
            OrderInfoRepository orderInfoRepository,
            UserRepository userRepository,
            PickerRepository pickerRepository
    ) {
        this.batchInfoRepository = batchInfoRepository;
        this.batchOrderInfoRepository = batchOrderInfoRepository;
        this.orderInfoRepository = orderInfoRepository;
        this.userRepository = userRepository;
        this.pickerRepository = pickerRepository;
    }

    @Transactional
    public BatchResponse createBatch(BatchRequest request, long actorId) {
        requireOrderManager(actorId);
        List<OrderInfo> orders = resolveOrdersForBatch(request.getOrderNumbers(), null);

        BatchInfo batchInfo = new BatchInfo(BatchStatus.CREATED);
        populateBatch(batchInfo, orders);

        return BatchResponse.from(batchInfoRepository.save(batchInfo));
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getBatches(long actorId) {
        requireLogin(actorId);
        return batchInfoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BatchResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BatchResponse getBatch(long batchId, long actorId) {
        requireLogin(actorId);
        return BatchResponse.from(getBatch(batchId));
    }

    @Transactional
    public BatchResponse updateBatch(long batchId, BatchRequest request, long actorId) {
        requireOrderManager(actorId);
        BatchInfo batchInfo = getBatch(batchId);
        ensureEditable(batchInfo);

        List<OrderInfo> orders = resolveOrdersForBatch(request.getOrderNumbers(), batchInfo.getId());
        batchInfo.clearOrders();
        populateBatch(batchInfo, orders);

        return BatchResponse.from(batchInfoRepository.save(batchInfo));
    }

    @Transactional
    public BatchResponse assignBatch(long batchId, long pickerId, long actorId) {
        User actor = requireLogin(actorId);
        BatchInfo batchInfo = getBatch(batchId);
        ensureAssignable(batchInfo);

        Picker picker = pickerRepository.findById(pickerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));

        if (actor.getRole() == Role.PICKER && !picker.getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pickers can only assign batches to themselves");
        }

        batchInfo.assignPicker(picker);
        syncOrdersForAssignment(batchInfo, picker);
        refreshBatchSnapshots(batchInfo);
        return BatchResponse.from(batchInfoRepository.save(batchInfo));
    }

    @Transactional
    public BatchResponse assignBatchToSelf(long batchId, long actorId) {
        User actor = requireLogin(actorId);
        Picker picker = pickerRepository.findByEmailIgnoreCase(actor.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Logged in user is not registered as a picker"
                ));
        BatchInfo batchInfo = getBatch(batchId);
        ensureAssignable(batchInfo);

        batchInfo.assignPicker(picker);
        syncOrdersForAssignment(batchInfo, picker);
        refreshBatchSnapshots(batchInfo);
        return BatchResponse.from(batchInfoRepository.save(batchInfo));
    }

    @Transactional
    public BatchResponse markBatchPicked(long batchId, long actorId) {
        User actor = requireLogin(actorId);
        BatchInfo batchInfo = getBatch(batchId);

        if (batchInfo.getStatus() != BatchStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch must be assigned before it can be picked");
        }

        if (batchInfo.getPicker() == null || !batchInfo.getPicker().getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned picker can mark the batch as picked");
        }

        for (OrderInfo orderInfo : getOrdersInBatch(batchInfo)) {
            if (orderInfo.getStatus() != OrderStatus.ASSIGNED_FOR_PICKING) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All orders in the batch must be in ASSIGNED_FOR_PICKING status"
                );
            }
            orderInfo.markPicked();
        }

        batchInfo.markPicked();
        refreshBatchSnapshots(batchInfo);
        return BatchResponse.from(batchInfoRepository.save(batchInfo));
    }

    @Transactional
    public void deleteBatch(long batchId, long actorId) {
        requireOrderManager(actorId);
        BatchInfo batchInfo = getBatch(batchId);

        if (batchInfo.getStatus() != BatchStatus.CREATED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assigned or picked batches cannot be deleted because picker assignments cannot be removed"
            );
        }

        batchInfoRepository.delete(batchInfo);
    }

    private void populateBatch(BatchInfo batchInfo, List<OrderInfo> orders) {
        for (OrderInfo orderInfo : orders) {
            for (OrderItemInfo item : orderInfo.getItems()) {
                batchInfo.addOrder(new BatchOrderInfo(orderInfo, item));
            }
        }
        batchInfo.setOrderCount(orders.size());
    }

    private List<OrderInfo> resolveOrdersForBatch(List<String> orderNumbers, Long currentBatchId) {
        Set<String> normalizedOrderNumbers = new LinkedHashSet<>();
        Set<Long> currentBatchOrderIds = currentBatchId == null
                ? Set.of()
                : batchOrderInfoRepository.findAllByBatchInfoIdOrderByOrderNumberAscIdAsc(currentBatchId).stream()
                .map(batchOrderInfo -> batchOrderInfo.getOrderInfo().getId())
                .collect(java.util.stream.Collectors.toSet());

        for (String orderNumber : orderNumbers) {
            String normalized = orderNumber == null ? "" : orderNumber.trim();
            if (!normalizedOrderNumbers.add(normalized.toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate order numbers are not allowed in a batch");
            }
        }

        List<OrderInfo> orders = orderNumbers.stream()
                .map(orderNumber -> orderNumber == null ? "" : orderNumber.trim())
                .map(this::getOrder)
                .toList();

        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one order is required");
        }

        String fcId = orders.getFirst().getFcId();
        String warehouseId = orders.getFirst().getWarehouseId();

        for (OrderInfo orderInfo : orders) {
            if (orderInfo.getStatus() == OrderStatus.BACK_ORDER) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Orders in BACK_ORDER status cannot be added to a batch"
                );
            }

            if (orderInfo.getStatus() != OrderStatus.CREATED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only orders in CREATED status can be added to a batch"
                );
            }

            if (orderInfo.getPicker() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned orders cannot be added to a batch");
            }

            boolean belongsToActiveBatch = batchOrderInfoRepository
                    .existsByOrderInfoAndBatchInfo_StatusIn(orderInfo, ACTIVE_BATCH_STATUSES);
            if (belongsToActiveBatch && !currentBatchOrderIds.contains(orderInfo.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Order " + orderInfo.getOrderNumber() + " is already part of an active batch"
                );
            }

            if (!orderInfo.getFcId().equalsIgnoreCase(fcId) || !orderInfo.getWarehouseId().equalsIgnoreCase(warehouseId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All orders in a batch must belong to the same warehouse and FC"
                );
            }
        }

        return orders;
    }

    private void syncOrdersForAssignment(BatchInfo batchInfo, Picker picker) {
        for (OrderInfo orderInfo : getOrdersInBatch(batchInfo)) {
            orderInfo.assignPicker(picker);
        }
    }

    private void refreshBatchSnapshots(BatchInfo batchInfo) {
        for (BatchOrderInfo batchOrderInfo : batchInfo.getOrders()) {
            OrderItemInfo item = batchOrderInfo.getOrderInfo().getItems().stream()
                    .filter(orderItemInfo -> orderItemInfo.getId() == batchOrderInfo.getOrderItemInfoId())
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch item could not be synchronized"));
            batchOrderInfo.syncFrom(batchOrderInfo.getOrderInfo(), item);
        }
    }

    private List<OrderInfo> getOrdersInBatch(BatchInfo batchInfo) {
        return batchInfo.getOrders().stream()
                .map(BatchOrderInfo::getOrderInfo)
                .distinct()
                .toList();
    }

    private void ensureAssignable(BatchInfo batchInfo) {
        if (batchInfo.getStatus() != BatchStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only created batches can be claimed");
        }
    }

    private void ensureEditable(BatchInfo batchInfo) {
        if (batchInfo.getStatus() != BatchStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only created batches can be updated");
        }
    }

    private BatchInfo getBatch(long batchId) {
        return batchInfoRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
    }

    private OrderInfo getOrder(String orderNumber) {
        return orderInfoRepository.findByOrderNumberIgnoreCase(orderNumber.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
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
}
