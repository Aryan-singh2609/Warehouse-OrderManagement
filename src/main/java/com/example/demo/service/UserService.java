package com.example.demo.service;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterUserRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    public static final String SUPER_ADMIN_EMAIL = "Admin@Example.com";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(RegisterUserRequest request, Role actorRole) {
        if (emailExists(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered");
        }

        Role role = resolveRoleForCreate(request, actorRole);
        String passwordHash = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getName(), request.getEmail(), passwordHash, role);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered");
        }
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        List<UserResponse> users = user.getRole() == Role.ASSOCIATE ? Collections.emptyList() : getUsers();
        return new LoginResponse(UserResponse.from(user), users);
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(long id, UpdateUserRequest request, Role actorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (actorRole != Role.SUPER_ADMIN && user.getRole() == Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admin can update the super admin account");
        }

        userRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> existing.getId() != id)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered");
                });

        Role role = resolveRoleForUpdate(user, request, actorRole);
        String passwordHash = request.getPassword() == null || request.getPassword().isBlank()
                ? user.getPasswordHash()
                : passwordEncoder.encode(request.getPassword());

        user.update(request.getName(), request.getEmail(), passwordHash, role);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(long id, Role actorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Super admin account cannot be deleted");
        }

        userRepository.deleteById(id);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    private Role resolveRoleForCreate(RegisterUserRequest request, Role actorRole) {
        if (actorRole != Role.SUPER_ADMIN) {
            return Role.ASSOCIATE;
        }

        Role requestedRole = request.getRole() == null ? Role.ASSOCIATE : request.getRole();
        validateSuperAdminRole(requestedRole, request.getEmail(), null);
        return requestedRole;
    }

    private Role resolveRoleForUpdate(User user, UpdateUserRequest request, Role actorRole) {
        if (actorRole != Role.SUPER_ADMIN) {
            return user.getRole();
        }

        Role requestedRole = request.getRole() == null ? user.getRole() : request.getRole();
        validateSuperAdminRole(requestedRole, request.getEmail(), user.getId());
        return requestedRole;
    }

    private void validateSuperAdminRole(Role role, String email, Long userId) {
        if (role != Role.SUPER_ADMIN) {
            return;
        }

        if (!SUPER_ADMIN_EMAIL.equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only Admin@Example.com can be super admin");
        }

        userRepository.findFirstByRole(Role.SUPER_ADMIN)
                .filter(existing -> userId == null || existing.getId() != userId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only one super admin account is allowed");
                });
    }
}
