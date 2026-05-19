package com.identio.mvp.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FacialTokenRequest(
        @NotNull(message = "userId is required")
        UUID userId
) {}
