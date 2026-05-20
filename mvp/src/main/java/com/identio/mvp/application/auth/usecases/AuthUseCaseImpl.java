package com.identio.mvp.application.auth.usecases;

import com.identio.mvp.application.auth.dto.*;
import com.identio.mvp.domain.user.entities.Role;
import com.identio.mvp.domain.user.entities.User;
import com.identio.mvp.domain.user.enums.UserStatus;
import com.identio.mvp.domain.user.repositories.RoleRepository;
import com.identio.mvp.domain.user.repositories.UserRepository;
import com.identio.mvp.infrastructure.auth.security.PasswordHashingService;
import com.identio.mvp.infrastructure.auth.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashingService passwordHashingService;
    private final TokenService tokenService;

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent())
            throw new IllegalArgumentException("Email is already in use");
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        User newUser = User.builder().fullName(request.fullName()).email(request.email())
                .passwordHash(passwordHashingService.encode(request.password())).status(UserStatus.PENDING)
                .roles(Set.of(userRole)).build();
        userRepository.save(newUser);
        return new TokenResponse(newUser.getId(), tokenService.generateAccessToken(newUser),
                tokenService.generateRefreshToken(newUser), newUser.getFullName(), newUser.getStatus().name());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordHashingService.matches(request.password(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid credentials");
        if (user.getStatus() == UserStatus.BLOCKED || user.getStatus() == UserStatus.DELETED)
            throw new DisabledException("Account is inactive");
        return new TokenResponse(user.getId(), tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user), user.getFullName(), user.getStatus().name());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            String subject = tokenService.extractSubjectFromRefreshToken(request.refreshToken());
            User user = userRepository.findById(UUID.fromString(subject))
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            if (user.getStatus() == UserStatus.BLOCKED || user.getStatus() == UserStatus.DELETED)
                throw new DisabledException("Account is inactive");
            return new TokenResponse(user.getId(), tokenService.generateAccessToken(user),
                    tokenService.generateRefreshToken(user), user.getFullName(), user.getStatus().name());
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse loginFacial(FacialTokenRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getStatus() != UserStatus.ENROLLED)
            throw new DisabledException("Account is inactive");
        return new TokenResponse(user.getId(), tokenService.generateShortLivedAccessToken(user), "", user.getFullName(),
                user.getStatus().name());
    }
}