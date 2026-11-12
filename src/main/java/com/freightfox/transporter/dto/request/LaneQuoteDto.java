package com.freightfox.transporter.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LaneQuoteDto {

    @NotNull(message = "Lane id is required")
    @Min(value = 1, message = "Lane id must be positive")
    private Integer laneId;

    @NotNull(message = "Quote is required")
    @Min(value = 0, message = "Quote must be non-negative")
    private Integer quote;

    public LaneQuoteDto() {
    }

    public LaneQuoteDto(Integer laneId, Integer quote) {
        this.laneId = laneId;
        this.quote = quote;
    }

    public Integer getLaneId() {
        return laneId;
    }

    public void setLaneId(Integer laneId) {
        this.laneId = laneId;
    }

    public Integer getQuote() {
        return quote;
    }

    public void setQuote(Integer quote) {
        this.quote = quote;
    }
}

