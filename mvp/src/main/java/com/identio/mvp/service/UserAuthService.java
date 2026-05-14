package com.identio.mvp.service;

import com.identio.mvp.dto.auth.LoginRequest;
import com.identio.mvp.dto.auth.RefreshTokenRequest;
import com.identio.mvp.dto.auth.TokenResponse;

import com.identio.mvp.dto.auth.RegisterRequest;

public interface UserAuthService {
    TokenResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refreshToken(RefreshTokenRequest request);
}
