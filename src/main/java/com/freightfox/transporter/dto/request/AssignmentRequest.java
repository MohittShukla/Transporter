package com.freightfox.transporter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Immutable DTO representing assignment optimization request using Java 17 record.
 */
public record AssignmentRequest(
        @NotNull(message = "maxTransporters is required")
        @Min(value = 1, message = "maxTransporters must be at least 1")
        Integer maxTransporters
) {
    public Integer getMaxTransporters() { return maxTransporters(); }
}
