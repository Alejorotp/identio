package com.identio.mvp.application.audit.usecases;

import com.identio.mvp.application.audit.dto.AuditLogResponse;
import com.identio.mvp.domain.audit.entities.AuditLog;
import com.identio.mvp.domain.audit.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAuditLogsUseCaseImpl implements GetAuditLogsUseCase {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(this::mapToResponse);
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUser() != null ? log.getUser().getId() : null,
                log.getEventType(),
                log.getTimestamp(),
                log.getDetails()
        );
    }
}
