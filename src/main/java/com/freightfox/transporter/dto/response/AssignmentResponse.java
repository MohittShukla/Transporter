package com.freightfox.transporter.dto.response;

import java.util.List;

/**
 * Immutable DTO representing optimization response using Java 17 record.
 */
public record AssignmentResponse(
        String status,
        long totalCost,
        List<AssignmentDetail> assignments,
        List<Integer> selectedTransporters
) {
    public String getStatus() { return status(); }
    public long getTotalCost() { return totalCost(); }
    public List<AssignmentDetail> getAssignments() { return assignments(); }
    public List<Integer> getSelectedTransporters() { return selectedTransporters(); }
}
