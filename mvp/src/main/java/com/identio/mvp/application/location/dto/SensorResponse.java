package com.identio.mvp.application.location.dto;

import com.identio.mvp.domain.location.enums.SensorStatus;
import java.util.Map;
import java.util.UUID;

public record SensorResponse(
        UUID id,
        String name,
        String macAddress,
        SensorStatus status,
        UUID locationId,
        Map<String, Object> config
) {}
