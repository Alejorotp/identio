package com.identio.mvp.application.user.dto;

import java.util.Set;

public record UpdateUserRequest(
        String fullName,
        String password,
        Set<String> roles
) {}
