package com.identio.mvp.config;

import com.identio.mvp.domain.entity.Role;
import com.identio.mvp.domain.entity.User;
import com.identio.mvp.domain.enums.UserStatus;
import com.identio.mvp.repository.RoleRepository;
import com.identio.mvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSetupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
                    .passwordHash(passwordEncoder.encode("admin123")) // Default password
                    .status(UserStatus.ENROLLED)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);
            log.info("Default admin user created successfully (email: admin@identio.com, password: admin123)");
        }
    }
}
