package com.freightfox.transporter.dto.response;

import java.util.List;

public class AssignmentResponse {

    private String status;
    private long totalCost;
    private List<AssignmentDetail> assignments;
    private List<Integer> selectedTransporters;

    public AssignmentResponse(String status, long totalCost,
                              List<AssignmentDetail> assignments,
                              List<Integer> selectedTransporters) {
        this.status = status;
        this.totalCost = totalCost;
        this.assignments = assignments;
        this.selectedTransporters = selectedTransporters;
    }

    public String getStatus() {
        return status;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public List<AssignmentDetail> getAssignments() {
        return assignments;
    }

    public List<Integer> getSelectedTransporters() {
        return selectedTransporters;
    }
}

