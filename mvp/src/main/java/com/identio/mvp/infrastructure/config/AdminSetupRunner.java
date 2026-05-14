package com.identio.mvp.infrastructure.config;

import com.identio.mvp.domain.user.entities.Role;
import com.identio.mvp.domain.user.entities.User;
import com.identio.mvp.domain.user.enums.UserStatus;
import com.identio.mvp.domain.user.repositories.RoleRepository;
import com.identio.mvp.domain.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import com.identio.mvp.infrastructure.auth.security.PasswordHashingService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSetupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashingService passwordHashingService;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@identio.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Admin user not found. Creating default admin user...");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Make sure Flyway V1 ran."));

            User adminUser = User.builder()
                    .fullName("System Administrator")
                    .email(adminEmail)
                    .passwordHash(passwordHashingService.encode("admin123"))
                    .status(UserStatus.ENROLLED)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);
            log.info("Default admin user created successfully (email: admin@identio.com, password: admin123)");
        }
    }
}
