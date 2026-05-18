package com.identio.mvp.application.audit.usecases;

import com.identio.mvp.application.audit.dto.AuditEvent;

public interface ProcessAuditEventUseCase {
    void process(AuditEvent event);
}
