package com.freightfox.transporter.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InputRequest {

    @NotEmpty(message = "Lanes list cannot be empty")
    @Valid
    private List<LaneDto> lanes;

    @NotEmpty(message = "Transporters list cannot be empty")
    @Valid
    private List<TransporterDto> transporters;

    public InputRequest() {
    }

    public InputRequest(List<LaneDto> lanes, List<TransporterDto> transporters) {
        this.lanes = lanes;
        this.transporters = transporters;
    }

    public List<LaneDto> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneDto> lanes) {
        this.lanes = lanes;
    }

    public List<TransporterDto> getTransporters() {
        return transporters;
    }

    public void setTransporters(List<TransporterDto> transporters) {
        this.transporters = transporters;
    }
}

