package com.freightfox.transporter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Immutable DTO representing input ingestion payload using Java 17 record.
 */
public record InputRequest(
        @NotEmpty(message = "Lanes list cannot be empty")
        @Valid
        List<LaneDto> lanes,

        @NotEmpty(message = "Transporters list cannot be empty")
        @Valid
        List<TransporterDto> transporters
) {
    public List<LaneDto> getLanes() { return lanes(); }
    public List<TransporterDto> getTransporters() { return transporters(); }
}
