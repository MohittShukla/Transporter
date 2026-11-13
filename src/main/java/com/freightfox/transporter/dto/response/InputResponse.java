package com.freightfox.transporter.dto.response;

/**
 * Immutable DTO representing input submission response using Java 17 record.
 */
public record InputResponse(String status, String message) {
    public String getStatus() { return status(); }
    public String getMessage() { return message(); }
}
