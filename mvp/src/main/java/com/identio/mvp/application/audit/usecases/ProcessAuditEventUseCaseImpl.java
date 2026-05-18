package com.identio.mvp.application.audit.usecases;

import com.identio.mvp.application.audit.dto.AuditEvent;
import com.identio.mvp.domain.audit.entities.AuditLog;
import com.identio.mvp.domain.audit.repositories.AuditLogRepository;
import com.identio.mvp.domain.user.entities.User;
import com.identio.mvp.domain.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAuditEventUseCaseImpl implements ProcessAuditEventUseCase {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void process(AuditEvent event) {
        log.info("Processing AuditEvent: {}", event);

        User user = null;
        if (event.userId() != null) {
            user = userRepository.findById(event.userId()).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .eventType(event.eventType())
                .details(event.details())
                .build();

        auditLogRepository.save(auditLog);
    }
}
