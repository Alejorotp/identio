package com.identio.mvp.service.impl;

import com.identio.mvp.domain.entity.Role;
import com.identio.mvp.domain.entity.User;
import com.identio.mvp.domain.enums.UserStatus;
import com.identio.mvp.dto.auth.LoginRequest;
import com.identio.mvp.dto.auth.RefreshTokenRequest;
import com.identio.mvp.dto.auth.RegisterRequest;
import com.identio.mvp.dto.auth.TokenResponse;
import com.identio.mvp.repository.RoleRepository;
import com.identio.mvp.repository.UserRepository;
import com.identio.mvp.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("Processing registration request for user: {}", request.email());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found in the system"));

        User newUser = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(UserStatus.ENROLLED) // Set to ENROLLED so they can log in immediately
                .roles(Set.of(userRole))
                .build();

        userRepository.save(newUser);
        log.info("User {} successfully registered", newUser.getId());
        
        return generateTokens(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for user: {}", request.email());
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ENROLLED) {
            log.warn("Login attempt for user with status {}: {}", user.getStatus(), request.email());
            throw new DisabledException("User account is not active");
        }

        log.info("User {} successfully authenticated", user.getId());
        return generateTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Processing refresh token request");

        try {
            Jwt jwt = jwtDecoder.decode(request.refreshToken());
            String type = jwt.getClaimAsString("type");
            
            if (!"refresh".equals(type)) {
                throw new BadCredentialsException("Invalid token type");
            }
            
            String subject = jwt.getSubject();
            if (subject == null) {
                throw new BadCredentialsException("Invalid refresh token");
            }

            UUID userId = UUID.fromString(subject);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            if (user.getStatus() != UserStatus.ENROLLED) {
                throw new DisabledException("User account is not active");
            }

            log.info("Refresh token validated for user: {}", userId);
            return generateTokens(user);
            
        } catch (Exception e) {
            log.warn("Invalid refresh token provided: {}", e.getMessage());
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
    }

    private TokenResponse generateTokens(User user) {
        Instant now = Instant.now();
        
        // Calculate midnight in the system default time zone for access token expiration
        Instant midnight = LocalDate.now(ZoneId.systemDefault())
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        // 7 days expiration for refresh token
        Instant refreshExp = now.plus(7, ChronoUnit.DAYS);

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("identio-auth")
                .issuedAt(now)
                .expiresAt(midnight)
                .subject(user.getId().toString())
                .claim("roles", roles)
                .claim("type", "access")
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("identio-auth")
                .issuedAt(now)
                .expiresAt(refreshExp)
                .subject(user.getId().toString())
                .claim("type", "refresh")
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        return new TokenResponse(accessToken, refreshToken);
    }
}
