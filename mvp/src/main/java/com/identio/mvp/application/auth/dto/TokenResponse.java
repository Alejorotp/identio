package com.identio.mvp.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Response containing authentication tokens and user information")
public record TokenResponse(
        @Schema(description = "The unique identifier of the authenticated user")
        UUID userId,
        @Schema(description = "JWT access token for API authorization")
        String accessToken,
        @Schema(description = "JWT refresh token for obtaining new access tokens")
        String refreshToken,
        @Schema(description = "Token type, typically Bearer")
        String tokenType
) {
    public TokenResponse(UUID userId, String accessToken, String refreshToken) {
        this(userId, accessToken, refreshToken, "Bearer");
    }
}
