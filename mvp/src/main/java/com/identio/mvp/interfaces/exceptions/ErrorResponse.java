package com.identio.mvp.interfaces.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}
