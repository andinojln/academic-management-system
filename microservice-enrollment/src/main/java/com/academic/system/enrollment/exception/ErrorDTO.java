package com.academic.system.enrollment.exception;

import java.time.LocalDateTime;

public record ErrorDTO(
        String message,
        int status,
        LocalDateTime timestamp
) {
    public ErrorDTO {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public ErrorDTO(String message, int status) {
        this(message, status, LocalDateTime.now());
    }
}
