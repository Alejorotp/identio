package com.identio.mvp.application.location.dto;

import java.util.Map;
import java.util.UUID;

public record UpdateSensorRequest(
        String name,
        UUID locationId,
        Map<String, Object> config
) {}
