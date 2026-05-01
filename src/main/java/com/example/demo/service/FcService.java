package com.example.demo.service;

import com.example.demo.dto.FcRequest;
import com.example.demo.dto.FcResponse;
import com.example.demo.entity.FcInfo;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.FcInfoRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FcService {

    private final FcInfoRepository fcInfoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FcService(FcInfoRepository fcInfoRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.fcInfoRepository = fcInfoRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FcResponse createFc(FcRequest request, long actorId) {
        requireManager(actorId);
        validateUniqueFcId(request.getFcId(), null);
        FcInfo fcInfo = fcInfoRepository.save(new FcInfo(
                request.getWarehouseId(),
                request.getFcId(),
                request.getLocation(),
                request.getLatitude(),
                request.getLongitude()
        ));
        return FcResponse.from(fcInfo);
    }

    @Transactional(readOnly = true)
    public List<FcResponse> getFcs(long actorId) {
        requireManager(actorId);
        return fcInfoRepository.findAll().stream().map(FcResponse::from).toList();
    }

    @Transactional
    public FcResponse updateFc(long id, FcRequest request, long actorId) {
        requireManager(actorId);
        FcInfo fcInfo = fcInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FC not found"));
        validateUniqueFcId(request.getFcId(), id);
        fcInfo.update(
                request.getWarehouseId(),
                request.getFcId(),
                request.getLocation(),
                request.getLatitude(),
                request.getLongitude()
        );
        return FcResponse.from(fcInfoRepository.save(fcInfo));
    }

    @Transactional
    public void deleteFc(long id, long actorId) {
        requireManager(actorId);
        FcInfo fcInfo = fcInfoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FC not found"));
        if (productRepository.existsByFcInfo_Id(fcInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FC cannot be deleted because products are assigned to it");
        }
        fcInfoRepository.deleteById(id);
    }

    private void validateUniqueFcId(String fcId, Long id) {
        fcInfoRepository.findByFcIdIgnoreCase(fcId)
                .filter(existing -> id == null || existing.getId() != id)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FC ID is already registered");
                });
    }

    private User requireManager(long actorId) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required"));
        if (actor.getRole() != Role.ASSOCIATE && actor.getRole() != Role.ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FC management is not allowed");
        }
        return actor;
    }
}
