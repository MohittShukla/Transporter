package com.freightfox.transporter.dto.response;

import java.time.LocalDateTime;

/**
 * Standard error response returned by the global exception handler.
 */
public class ErrorResponse {

    private String status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

