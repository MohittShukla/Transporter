package com.freightfox.transporter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TransporterDto {

    @NotNull(message = "Transporter id is required")
    @Min(value = 1, message = "Transporter id must be positive")
    private Integer id;

    @NotBlank(message = "Transporter name is required")
    private String name;

    @NotEmpty(message = "Lane quotes cannot be empty")
    @Valid
    private List<LaneQuoteDto> laneQuotes;

    public TransporterDto() {
    }

    public TransporterDto(Integer id, String name, List<LaneQuoteDto> laneQuotes) {
        this.id = id;
        this.name = name;
        this.laneQuotes = laneQuotes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LaneQuoteDto> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuoteDto> laneQuotes) {
        this.laneQuotes = laneQuotes;
    }
}

