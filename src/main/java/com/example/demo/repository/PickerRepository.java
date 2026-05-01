package com.example.demo.repository;

import com.example.demo.entity.Picker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickerRepository extends JpaRepository<Picker, Long> {
    Optional<Picker> findByEmailIgnoreCase(String email);
    Optional<Picker> findByEmployeeIdIgnoreCase(String employeeId);
}
