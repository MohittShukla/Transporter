package com.freightfox.transporter.dto.response;

public class AssignmentDetail {

    private int laneId;
    private int transporterId;

    public AssignmentDetail(int laneId, int transporterId) {
        this.laneId = laneId;
        this.transporterId = transporterId;
    }

    public int getLaneId() {
        return laneId;
    }

    public int getTransporterId() {
        return transporterId;
    }
}

