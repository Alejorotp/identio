package com.identio.mvp.application.user.usecases;

import com.identio.mvp.application.user.dto.CreateUserRequest;
import com.identio.mvp.application.user.dto.UpdateUserRequest;
import com.identio.mvp.application.user.dto.UpdateUserStatusRequest;
import com.identio.mvp.application.user.dto.UserResponse;
import com.identio.mvp.domain.user.entities.Role;
import com.identio.mvp.domain.user.entities.User;
import com.identio.mvp.domain.user.enums.UserStatus;
import com.identio.mvp.domain.user.repositories.RoleRepository;
import com.identio.mvp.domain.user.repositories.UserRepository;
import com.identio.mvp.infrastructure.auth.security.PasswordHashingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashingService passwordHashingService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Set<Role> roles = new HashSet<>();
        if (request.roles() == null || request.roles().isEmpty()) {
            roles.add(roleRepository.findByName("ROLE_USER").orElseThrow());
        } else {
            for (String roleName : request.roles()) {
                roles.add(roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)));
            }
        }

        User newUser = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordHashingService.encode(request.password()))
                .status(UserStatus.ENROLLED)
                .roles(roles)
                .build();

        userRepository.save(newUser);
        return mapToResponse(newUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordHashingService.encode(request.password()));
        }
        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.roles()) {
                roles.add(roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)));
            }
            user.setRoles(roles);
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(UUID id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(request.status());
        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id, UserStatus status) {
        if (status != UserStatus.BLOCKED && status != UserStatus.DELETED) {
            throw new IllegalArgumentException("Delete status must be BLOCKED or DELETED");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getStatus(), roleNames);
    }
}
