package com.identio.mvp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.ServerBuilderCustomizer;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import java.util.concurrent.Executors;

@Configuration
@SuppressWarnings("rawtypes")
public class GrpcSecurityExecutorConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public ServerBuilderCustomizer securityExecutorCustomizer() {
        return builder -> builder.executor(
            new DelegatingSecurityContextExecutor(Executors.newCachedThreadPool())
        );
    }
}
