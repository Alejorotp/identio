package com.identio.mvp.application.location.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CreateLocationRequest(
        @NotBlank String name,
        String address,
        Double latitude,
        Double longitude,
        Map<String, Object> metadata
) {}
