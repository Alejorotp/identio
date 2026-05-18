package com.identio.mvp.interfaces.audit.rabbitmq;

import com.identio.mvp.application.audit.dto.AuditEvent;
import com.identio.mvp.application.audit.usecases.ProcessAuditEventUseCase;
import com.identio.mvp.infrastructure.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final ProcessAuditEventUseCase processAuditEventUseCase;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void onMessage(AuditEvent event) {
        log.info("Received AuditEvent from queue: {}", event);
        try {
            processAuditEventUseCase.process(event);
        } catch (Exception e) {
            log.error("Failed to process AuditEvent. Will be retried or sent to DLQ.", e);
            throw e; // Throwing triggers the retry/DLQ mechanism
        }
    }
}
