package com.identio.mvp.application.location.dto;

import com.identio.mvp.domain.location.enums.SensorStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateSensorRequest(
        @NotBlank String name,
        @NotBlank String macAddress,
        @NotNull SensorStatus status,
        UUID locationId,
        Map<String, Object> config
) {}
