package com.freightfox.transporter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Immutable DTO representing a lane freight quote using Java 17 record.
 */
public record LaneQuoteDto(
        @NotNull(message = "Lane id is required")
        @Min(value = 1, message = "Lane id must be positive")
        Integer laneId,

        @NotNull(message = "Quote is required")
        @Min(value = 0, message = "Quote must be non-negative")
        Integer quote
) {
    public Integer getLaneId() { return laneId(); }
    public Integer getQuote() { return quote(); }
}
