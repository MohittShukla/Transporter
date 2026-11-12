package com.freightfox.transporter.model;

import java.util.List;

/**
 * Domain model representing a transporter and its freight quotes for serviced lanes.
 */
public class Transporter {

    private int id;
    private String name;
    private List<LaneQuote> laneQuotes;

    public Transporter() {
    }

    public Transporter(int id, String name, List<LaneQuote> laneQuotes) {
        this.id = id;
        this.name = name;
        this.laneQuotes = laneQuotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LaneQuote> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuote> laneQuotes) {
        this.laneQuotes = laneQuotes;
    }
}
