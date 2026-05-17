package com.identio.mvp.application.location.dto;

import java.util.Map;

public record UpdateLocationRequest(
        String name,
        String address,
        Double latitude,
        Double longitude,
        Map<String, Object> metadata
) {}
