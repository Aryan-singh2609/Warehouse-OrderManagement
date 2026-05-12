package com.example.demo.service;

import com.example.demo.dto.PickerRequest;
import com.example.demo.dto.PickerResponse;
import com.example.demo.entity.Picker;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.BatchInfoRepository;
import com.example.demo.repository.OrderInfoRepository;
import com.example.demo.repository.PickerRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PickerService {

    private final PickerRepository pickerRepository;
    private final UserRepository userRepository;
    private final OrderInfoRepository orderInfoRepository;
    private final BatchInfoRepository batchInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PickerService(
            PickerRepository pickerRepository,
            UserRepository userRepository,
            OrderInfoRepository orderInfoRepository,
            BatchInfoRepository batchInfoRepository
    ) {
        this.pickerRepository = pickerRepository;
        this.userRepository = userRepository;
        this.orderInfoRepository = orderInfoRepository;
        this.batchInfoRepository = batchInfoRepository;
    }

    @Transactional
    public PickerResponse createPicker(PickerRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required for new pickers");
        }

        validateUniquePickerFields(request.getEmail(), request.getEmployeeId(), null, null);

        Picker picker = new Picker(request.getName(), request.getEmail(), request.getEmployeeId());
        User pickerUser = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.PICKER
        );

        try {
            userRepository.save(pickerUser);
            return PickerResponse.from(pickerRepository.save(picker));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Picker email or employee ID already exists");
        }
    }

    @Transactional(readOnly = true)
    public List<PickerResponse> getPickers() {
        return pickerRepository.findAll().stream()
                .map(PickerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PickerResponse getPicker(long id) {
        return PickerResponse.from(findPicker(id));
    }

    @Transactional
    public PickerResponse updatePicker(long id, PickerRequest request) {
        Picker picker = findPicker(id);
        User pickerUser = userRepository.findByEmailIgnoreCase(picker.getEmail())
                .filter(existing -> existing.getRole() == Role.PICKER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Linked picker login not found"));

        validateUniquePickerFields(request.getEmail(), request.getEmployeeId(), id, pickerUser.getId());

        picker.update(request.getName(), request.getEmail(), request.getEmployeeId());
        String passwordHash = request.getPassword() == null || request.getPassword().isBlank()
                ? pickerUser.getPasswordHash()
                : passwordEncoder.encode(request.getPassword());
        pickerUser.update(request.getName(), request.getEmail(), passwordHash, Role.PICKER);

        try {
            userRepository.save(pickerUser);
            return PickerResponse.from(pickerRepository.save(picker));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Picker email or employee ID already exists");
        }
    }

    @Transactional
    public void deletePicker(long id) {
        Picker picker = findPicker(id);
        if (orderInfoRepository.existsByPicker_Id(id) || batchInfoRepository.existsByPicker_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Picker cannot be deleted because orders or batches are assigned to it"
            );
        }

        userRepository.findByEmailIgnoreCase(picker.getEmail())
                .filter(existing -> existing.getRole() == Role.PICKER)
                .ifPresent(userRepository::delete);
        pickerRepository.delete(picker);
    }

    public Picker findPicker(long id) {
        return pickerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));
    }

    private void validateUniquePickerFields(String email, String employeeId, Long pickerId, Long userId) {
        pickerRepository.findByEmailIgnoreCase(email)
                .filter(existing -> pickerId == null || existing.getId() != pickerId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
                });

        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> userId == null || existing.getId() != userId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
                });

        pickerRepository.findByEmployeeIdIgnoreCase(employeeId)
                .filter(existing -> pickerId == null || existing.getId() != pickerId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID already exists");
                });
    }
}
