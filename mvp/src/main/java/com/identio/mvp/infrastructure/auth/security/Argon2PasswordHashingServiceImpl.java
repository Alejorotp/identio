package com.identio.mvp.infrastructure.auth.security;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class Argon2PasswordHashingServiceImpl implements PasswordHashingService {
    private final PasswordEncoder passwordEncoder;
    @Override public String encode(String rawPassword) { return passwordEncoder.encode(rawPassword); }
    @Override public boolean matches(String rawPassword, String encodedPassword) { return passwordEncoder.matches(rawPassword, encodedPassword); }
}