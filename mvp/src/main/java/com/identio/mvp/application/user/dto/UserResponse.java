package com.identio.mvp.application.user.dto;

import com.identio.mvp.domain.user.enums.UserStatus;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        UserStatus status,
        Set<String> roles
) {}
