package com.identio.mvp.infrastructure.auth.security;
public interface PasswordHashingService {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}