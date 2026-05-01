package com.example.demo.service;

import com.example.demo.dto.PickerRequest;
import com.example.demo.dto.PickerResponse;
import com.example.demo.entity.Picker;
import com.example.demo.repository.PickerRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PickerService {

    private final PickerRepository pickerRepository;

    public PickerService(PickerRepository pickerRepository) {
        this.pickerRepository = pickerRepository;
    }

    @Transactional
    public PickerResponse createPicker(PickerRequest request) {
        if (pickerRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        if (pickerRepository.findByEmployeeIdIgnoreCase(request.getEmployeeId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID already exists");
        }

        Picker picker = new Picker(request.getName(), request.getEmail(), request.getEmployeeId());
        return PickerResponse.from(pickerRepository.save(picker));
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

        pickerRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(p -> p.getId() != id)
                .ifPresent(p -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists"); });

        pickerRepository.findByEmployeeIdIgnoreCase(request.getEmployeeId())
                .filter(p -> p.getId() != id)
                .ifPresent(p -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID already exists"); });

        picker.update(request.getName(), request.getEmail(), request.getEmployeeId());
        return PickerResponse.from(pickerRepository.save(picker));
    }

    @Transactional
    public void deletePicker(long id) {
        Picker picker = findPicker(id);
        pickerRepository.delete(picker);
    }

    public Picker findPicker(long id) {
        return pickerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));
    }

    public Picker findByEmail(String email) {
        return pickerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Picker not found"));
    }
}
