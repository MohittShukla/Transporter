package com.freightfox.transporter.model;

/**
 * Domain model representing a freight rate quote for a specific trade lane.
 */
public class LaneQuote {

    private int laneId;
    private int quote;

    public LaneQuote() {
    }

    public LaneQuote(int laneId, int quote) {
        this.laneId = laneId;
        this.quote = quote;
    }

    public int getLaneId() {
        return laneId;
    }

    public void setLaneId(int laneId) {
        this.laneId = laneId;
    }

    public int getQuote() {
        return quote;
    }

    public void setQuote(int quote) {
        this.quote = quote;
    }
}
