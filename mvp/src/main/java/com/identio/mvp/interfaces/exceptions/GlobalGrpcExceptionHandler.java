package com.identio.mvp.interfaces.exceptions;

import io.grpc.Status;
import io.grpc.StatusException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler implements GrpcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalGrpcExceptionHandler.class);

    @Override
    public StatusException handleException(Throwable ex) {
        if (ex instanceof ResourceNotFoundException) {
            log.warn("gRPC Resource not found: {}", ex.getMessage());
            return Status.NOT_FOUND.withDescription(ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof BusinessValidationException || ex instanceof IllegalArgumentException) {
            log.warn("gRPC Business validation failed: {}", ex.getMessage());
            return Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof ConstraintViolationException) {
            log.warn("gRPC Constraint violation: {}", ex.getMessage());
            return Status.INVALID_ARGUMENT.withDescription("Validation failed: " + ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof AuthenticationException) {
            log.warn("gRPC Authentication failed: {}", ex.getMessage());
            return Status.UNAUTHENTICATED.withDescription(ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof AccessDeniedException) {
            log.warn("gRPC Access denied: {}", ex.getMessage());
            return Status.PERMISSION_DENIED.withDescription("You do not have permission to access this resource.").withCause(ex).asException();
        } else {
            log.error("gRPC Unhandled exception occurred", ex);
            return Status.INTERNAL.withDescription("An unexpected error occurred.").withCause(ex).asException();
        }
    }
}
