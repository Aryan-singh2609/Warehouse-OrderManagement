package com.example.demo;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        User superAdmin = userRepository.findByEmailIgnoreCase(UserService.SUPER_ADMIN_EMAIL)
                .map(user -> {
                    user.update("Super Admin", UserService.SUPER_ADMIN_EMAIL, user.getPasswordHash(), Role.SUPER_ADMIN);
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(new User(
                        "Super Admin",
                        UserService.SUPER_ADMIN_EMAIL,
                        passwordEncoder.encode("Admin@123"),
                        Role.SUPER_ADMIN
                )));

        userRepository.findAllByRole(Role.SUPER_ADMIN)
                .stream()
                .filter(existing -> existing.getId() != superAdmin.getId())
                .forEach(existing -> {
                    existing.update(
                            existing.getName(),
                            existing.getEmail(),
                            existing.getPasswordHash(),
                            Role.ADMIN
                    );
                    userRepository.save(existing);
                });

        userRepository.findByEmailIgnoreCase("associate@example.com")
                .orElseGet(() -> userRepository.save(new User(
                        "Associate User",
                        "associate@example.com",
                        passwordEncoder.encode("Associate@123"),
                        Role.ASSOCIATE
                )));
    }
}
