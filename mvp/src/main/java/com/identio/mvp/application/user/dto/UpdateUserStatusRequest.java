package com.identio.mvp.application.user.dto;

import com.identio.mvp.domain.user.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull UserStatus status
) {}
