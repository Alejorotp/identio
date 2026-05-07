package com.identio.mvp.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler implements GrpcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalGrpcExceptionHandler.class);

    @Override
    public StatusException handleException(Throwable ex) {
        if (ex instanceof ResourceNotFoundException) {
            log.warn("gRPC Resource not found: {}", ex.getMessage());
            return Status.NOT_FOUND.withDescription(ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof BusinessValidationException) {
            log.warn("gRPC Business validation failed: {}", ex.getMessage());
            return Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).withCause(ex).asException();
        } else if (ex instanceof AccessDeniedException) {
            log.warn("gRPC Access denied: {}", ex.getMessage());
            return Status.PERMISSION_DENIED.withDescription("You do not have permission to access this resource.").withCause(ex).asException();
        } else {
            log.error("gRPC Unhandled exception occurred", ex);
            return Status.INTERNAL.withDescription("An unexpected error occurred.").withCause(ex).asException();
        }
    }
}
