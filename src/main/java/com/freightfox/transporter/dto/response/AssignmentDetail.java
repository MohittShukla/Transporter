package com.freightfox.transporter.dto.response;

/**
 * Immutable DTO representing a lane-to-transporter assignment using Java 17 record.
 */
public record AssignmentDetail(int laneId, int transporterId) {
    public int getLaneId() { return laneId(); }
    public int getTransporterId() { return transporterId(); }
}
