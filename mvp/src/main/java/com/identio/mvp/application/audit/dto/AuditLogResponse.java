package com.identio.mvp.application.audit.dto;

import com.identio.mvp.domain.audit.enums.EventType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID userId,
        EventType eventType,
        Instant timestamp,
        Map<String, Object> details
) {}
