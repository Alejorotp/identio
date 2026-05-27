package com.identio.mvp.application.audit.usecases;

import com.identio.mvp.application.audit.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetAuditLogsUseCase {
    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
    Page<AuditLogResponse> getAuditLogsByUserId(UUID userId, Pageable pageable);
}
