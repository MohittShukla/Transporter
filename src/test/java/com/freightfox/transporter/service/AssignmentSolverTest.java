package com.freightfox.transporter.service;

import com.freightfox.transporter.dto.response.AssignmentDetail;
import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.exception.NoFeasibleSolutionException;
import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.LaneQuote;
import com.freightfox.transporter.model.Transporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the core assignment solver.
 */
class AssignmentSolverTest {

    private final AssignmentSolver solver = new AssignmentSolver();

    // ── Helper methods to reduce boilerplate ──

    private Lane lane(int id, String origin, String dest) {
        return new Lane(id, origin, dest);
    }

    private Transporter transporter(int id, String name, int[]... quotes) {
        List<LaneQuote> laneQuotes = Arrays.stream(quotes)
                .map(q -> new LaneQuote(q[0], q[1]))
                .collect(Collectors.toList());
        return new Transporter(id, name, laneQuotes);
    }

    // ── Test Case 1 from assignment (5 lanes, 7 transporters, max 3) ──

    @Test
    @DisplayName("Test Case 1: 5 lanes, 7 transporters, maxTransporters=3")
    void testCase1_fromAssignment() {
        List<Lane> lanes = List.of(
                lane(1, "Mumbai", "Delhi"),
                lane(2, "Delhi", "Bangalore"),
                lane(3, "Chennai", "Kolkata"),
                lane(4, "Pune", "Hyderabad"),
                lane(5, "Ahmedabad", "Jaipur")
        );

        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 20835}, new int[]{2, 10512}, new int[]{3, 22105}, new int[]{4, 42481}, new int[]{5, 19862}),
                transporter(2, "T2", new int[]{1, 48844}, new int[]{2, 31326}, new int[]{3, 18640}, new int[]{4, 45828}, new int[]{5, 18297}),
                transporter(3, "T3", new int[]{1, 39020}, new int[]{2, 20648}, new int[]{3, 31438}, new int[]{4, 36447}, new int[]{5, 12789}),
                transporter(4, "T4", new int[]{1, 14400}, new int[]{2, 44514}, new int[]{3, 14316}, new int[]{4, 10678}, new int[]{5, 13032}),
                transporter(5, "T5", new int[]{1, 11601}, new int[]{2, 19760}, new int[]{3, 40870}, new int[]{4, 20635}, new int[]{5, 26421}),
                transporter(6, "T6", new int[]{1, 35095}, new int[]{2, 12494}, new int[]{3, 17808}, new int[]{4, 36210}, new int[]{5, 39444}),
                transporter(7, "T7", new int[]{1, 26070}, new int[]{2, 41098}, new int[]{3, 20932}, new int[]{4, 16897}, new int[]{5, 27938})
        );

        AssignmentResponse result = solver.solve(lanes, transporters, 3);

        assertEquals("success", result.getStatus());
        // All 5 lanes must be assigned
        assertEquals(5, result.getAssignments().size());
        // At most 3 transporters used
        assertTrue(result.getSelectedTransporters().size() <= 3);
        // Verify every lane is covered
        List<Integer> assignedLaneIds = result.getAssignments().stream()
                .map(AssignmentDetail::getLaneId).sorted().collect(Collectors.toList());
        assertEquals(List.of(1, 2, 3, 4, 5), assignedLaneIds);
        assertEquals(60139, result.getTotalCost());
        assertEquals(List.of(1, 4, 5), result.getSelectedTransporters());
    }

    // ── Test Case 2 from assignment (sparse quotes) ──

    @Test
    @DisplayName("Test Case 2: sparse quotes, 5 lanes, 4 transporters, maxTransporters=3")
    void testCase2_sparseQuotes() {
        List<Lane> lanes = List.of(
                lane(1, "Chandigarh", "Shimla"),
                lane(2, "Agra", "Kanpur"),
                lane(3, "Varanasi", "Gorakhpur"),
                lane(4, "Amritsar", "Ludhiana"),
                lane(5, "Coimbatore", "Madurai")
        );

        List<Transporter> transporters = List.of(
                transporter(1, "X", new int[]{1, 1000}, new int[]{2, 1500}),
                transporter(2, "Y", new int[]{3, 2000}, new int[]{4, 1800}),
                transporter(3, "Z", new int[]{5, 1200}),
                transporter(4, "W", new int[]{1, 1100}, new int[]{4, 1750})
        );

        AssignmentResponse result = solver.solve(lanes, transporters, 3);

        assertEquals("success", result.getStatus());
        assertEquals(5, result.getAssignments().size());
        assertTrue(result.getSelectedTransporters().size() <= 3);
        assertEquals(7500, result.getTotalCost());
        assertEquals(List.of(1, 2, 3), result.getSelectedTransporters());
    }

    // ── Edge cases ──

    @Test
    @DisplayName("Single lane, single transporter")
    void singleLaneSingleTransporter() {
        List<Lane> lanes = List.of(lane(1, "A", "B"));
        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 5000})
        );

        AssignmentResponse result = solver.solve(lanes, transporters, 1);

        assertEquals(5000, result.getTotalCost());
        assertEquals(1, result.getAssignments().size());
        assertEquals(List.of(1), result.getSelectedTransporters());
    }

    @Test
    @DisplayName("maxTransporters > available transporters uses all if needed")
    void maxTransportersExceedsAvailable() {
        List<Lane> lanes = List.of(lane(1, "A", "B"), lane(2, "C", "D"));
        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 3000}),
                transporter(2, "T2", new int[]{2, 4000})
        );

        // maxTransporters = 10 but only 2 exist
        AssignmentResponse result = solver.solve(lanes, transporters, 10);

        assertEquals(7000, result.getTotalCost());
        assertEquals(2, result.getAssignments().size());
    }

    @Test
    @DisplayName("Infeasible: lane not covered by any transporter")
    void infeasible_uncoveredLane() {
        List<Lane> lanes = List.of(lane(1, "A", "B"), lane(2, "C", "D"));
        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 3000})
                // No one covers lane 2!
        );

        assertThrows(NoFeasibleSolutionException.class,
                () -> solver.solve(lanes, transporters, 3));
    }

    @Test
    @DisplayName("Infeasible: maxTransporters too small to cover all lanes")
    void infeasible_notEnoughTransporters() {
        List<Lane> lanes = List.of(lane(1, "A", "B"), lane(2, "C", "D"), lane(3, "E", "F"));
        // Each transporter covers only 1 lane, but we can only pick 2
        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 1000}),
                transporter(2, "T2", new int[]{2, 2000}),
                transporter(3, "T3", new int[]{3, 3000})
        );

        assertThrows(NoFeasibleSolutionException.class,
                () -> solver.solve(lanes, transporters, 2));
    }

    @Test
    @DisplayName("Picks cheaper combo over more expensive one")
    void picksCheaperCombo() {
        List<Lane> lanes = List.of(lane(1, "A", "B"), lane(2, "C", "D"));
        List<Transporter> transporters = List.of(
                transporter(1, "T1", new int[]{1, 100}, new int[]{2, 100}),   // covers both for 200
                transporter(2, "T2", new int[]{1, 50}, new int[]{2, 500}),    // lane 1 cheap but lane 2 expensive
                transporter(3, "T3", new int[]{1, 500}, new int[]{2, 50})     // lane 2 cheap but lane 1 expensive
        );

        AssignmentResponse result = solver.solve(lanes, transporters, 2);

        // Best with 2: T2 for lane 1 (50) + T3 for lane 2 (50) = 100
        // Best with 1: T1 for both = 200
        assertEquals(100, result.getTotalCost());
    }
}

