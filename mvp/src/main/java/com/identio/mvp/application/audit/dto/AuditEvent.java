package com.identio.mvp.application.audit.dto;

import com.identio.mvp.domain.audit.enums.EventType;

import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID userId,
        EventType eventType,
        Map<String, Object> details
) {}
