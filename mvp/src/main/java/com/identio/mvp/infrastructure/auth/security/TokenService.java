package com.identio.mvp.infrastructure.auth.security;
import com.identio.mvp.domain.user.entities.User;
public interface TokenService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String generateShortLivedAccessToken(User user);
    String extractSubjectFromRefreshToken(String refreshToken);
}