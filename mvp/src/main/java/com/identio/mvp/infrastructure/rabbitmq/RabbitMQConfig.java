package com.identio.mvp.infrastructure.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String AUDIT_EXCHANGE = "audit.exchange";
    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String AUDIT_ROUTING_KEY = "audit.routing.key";
    
    public static final String AUDIT_DLQ = "audit.dlq";
    public static final String AUDIT_DLQ_ROUTING_KEY = "audit.dlq.routing.key";

    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange(AUDIT_EXCHANGE);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE)
                .withArgument("x-dead-letter-exchange", AUDIT_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AUDIT_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue auditDlq() {
        return QueueBuilder.durable(AUDIT_DLQ).build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue()).to(auditExchange()).with(AUDIT_ROUTING_KEY);
    }

    @Bean
    public Binding auditDlqBinding() {
        return BindingBuilder.bind(auditDlq()).to(auditExchange()).with(AUDIT_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
