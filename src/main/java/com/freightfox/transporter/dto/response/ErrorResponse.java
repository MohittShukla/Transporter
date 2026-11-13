package com.freightfox.transporter.dto.response;

import java.time.LocalDateTime;

/**
 * Standard error response returned by the global exception handler using Java 17 record.
 */
public record ErrorResponse(
        String status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(String status, String message) {
        this(status, message, LocalDateTime.now());
    }

    public String getStatus() { return status(); }
    public String getMessage() { return message(); }
    public LocalDateTime getTimestamp() { return timestamp(); }
}
