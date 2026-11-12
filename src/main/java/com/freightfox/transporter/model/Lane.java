package com.freightfox.transporter.model;

/**
 * Domain model representing a shipping trade lane from origin to destination.
 */
public class Lane {

    private int id;
    private String origin;
    private String destination;

    public Lane() {
    }

    public Lane(int id, String origin, String destination) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
