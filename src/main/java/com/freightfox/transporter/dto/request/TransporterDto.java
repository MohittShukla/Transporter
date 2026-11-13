package com.freightfox.transporter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Immutable DTO representing transporter details and quotes using Java 17 record.
 */
public record TransporterDto(
        @NotNull(message = "Transporter id is required")
        @Min(value = 1, message = "Transporter id must be positive")
        Integer id,

        @NotBlank(message = "Transporter name is required")
        String name,

        @NotEmpty(message = "Lane quotes cannot be empty")
        @Valid
        List<LaneQuoteDto> laneQuotes
) {
    public Integer getId() { return id(); }
    public String getName() { return name(); }
    public List<LaneQuoteDto> getLaneQuotes() { return laneQuotes(); }
}
