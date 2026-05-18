package com.identio.mvp.infrastructure.rabbitmq;

import com.identio.mvp.application.audit.dto.AuditEvent;
import com.identio.mvp.application.audit.ports.PublishAuditEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQAuditPublisher implements PublishAuditEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(AuditEvent event) {
        log.info("Publishing AuditEvent to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUDIT_EXCHANGE,
                RabbitMQConfig.AUDIT_ROUTING_KEY,
                event
        );
    }
}
