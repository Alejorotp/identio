package com.identio.mvp.application.auth.usecases;

import com.identio.mvp.application.auth.dto.LoginRequest;
import com.identio.mvp.application.auth.dto.RefreshTokenRequest;
import com.identio.mvp.application.auth.dto.TokenResponse;
import com.identio.mvp.application.auth.dto.RegisterRequest;
import jakarta.validation.Valid;

public interface AuthUseCase {
    TokenResponse register(@Valid RegisterRequest request);
    TokenResponse login(@Valid LoginRequest request);
    TokenResponse refreshToken(@Valid RefreshTokenRequest request);
}
