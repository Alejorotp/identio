package com.identio.mvp.application.location.dto;

import com.identio.mvp.domain.location.enums.SensorStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSensorStatusRequest(
        @NotNull SensorStatus status
) {}
