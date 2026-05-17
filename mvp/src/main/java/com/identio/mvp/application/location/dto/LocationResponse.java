package com.identio.mvp.application.location.dto;

import java.util.Map;
import java.util.UUID;

public record LocationResponse(
        UUID id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Map<String, Object> metadata
) {}
