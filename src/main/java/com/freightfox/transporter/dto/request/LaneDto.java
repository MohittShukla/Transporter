package com.freightfox.transporter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Immutable DTO representing a trade lane input using Java 17 record.
 */
public record LaneDto(
        @NotNull(message = "Lane id is required")
        @Min(value = 1, message = "Lane id must be positive")
        Integer id,

        @NotBlank(message = "Origin is required")
        String origin,

        @NotBlank(message = "Destination is required")
        String destination
) {
    public Integer getId() { return id(); }
    public String getOrigin() { return origin(); }
    public String getDestination() { return destination(); }
}
