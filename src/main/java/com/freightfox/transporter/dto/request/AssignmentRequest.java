package com.freightfox.transporter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AssignmentRequest {

    @NotNull(message = "maxTransporters is required")
    @Min(value = 1, message = "maxTransporters must be at least 1")
    private Integer maxTransporters;

    public AssignmentRequest() {
    }

    public AssignmentRequest(Integer maxTransporters) {
        this.maxTransporters = maxTransporters;
    }

    public Integer getMaxTransporters() {
        return maxTransporters;
    }

    public void setMaxTransporters(Integer maxTransporters) {
        this.maxTransporters = maxTransporters;
    }
}

