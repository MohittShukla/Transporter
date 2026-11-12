package com.freightfox.transporter.service;

import com.freightfox.transporter.dto.response.AssignmentDetail;
import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.exception.NoFeasibleSolutionException;
import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.LaneQuote;
import com.freightfox.transporter.model.Transporter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core assignment solver using combinatorial enumeration and greedy lane assignment.
 * Finds the cost-minimized assignment adhering to the maximum transporter constraint.
 */
@Service
public class AssignmentSolver {

    /**
     * Finds the optimal assignment of transporters to lanes.
     *
     * @param lanes            list of trade lanes to cover
     * @param transporters     list of available transporters with quotes
     * @param maxTransporters  maximum allowed transporters
     * @return AssignmentResponse containing itemized assignments and total cost
     * @throws NoFeasibleSolutionException if no combination covers all lanes
     */
    public AssignmentResponse solve(List<Lane> lanes, List<Transporter> transporters,
                                    int maxTransporters) {

        Map<Integer, Map<Integer, Integer>> quoteLookup = buildQuoteLookup(transporters);

        int numTransporters = transporters.size();
        int effectiveMax = Math.min(maxTransporters, numTransporters);

        long bestCost = Long.MAX_VALUE;
        List<AssignmentDetail> bestAssignments = null;
        List<Integer> bestTransporterIds = null;

        List<Integer> transporterIds = transporters.stream()
                .map(Transporter::getId)
                .collect(Collectors.toList());

        for (int size = 1; size <= effectiveMax; size++) {
            List<List<Integer>> combinations = generateCombinations(transporterIds, size);

            for (List<Integer> combo : combinations) {
                AssignmentResult result = tryAssignment(lanes, combo, quoteLookup);

                if (result == null) {
                    continue;
                }

                // Minimize total cost first; tie-break by selecting combination with more transporters
                if (result.totalCost < bestCost ||
                    (result.totalCost == bestCost && combo.size() > (bestTransporterIds != null ? bestTransporterIds.size() : 0))) {
                    bestCost = result.totalCost;
                    bestAssignments = result.assignments;
                    bestTransporterIds = combo;
                }
            }
        }

        if (bestAssignments == null) {
            throw new NoFeasibleSolutionException(
                    "No feasible assignment exists with at most " + maxTransporters +
                    " transporters covering all " + lanes.size() + " lanes.");
        }

        List<Integer> actuallyUsed = bestAssignments.stream()
                .map(AssignmentDetail::getTransporterId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new AssignmentResponse("success", bestCost, bestAssignments, actuallyUsed);
    }

    private Map<Integer, Map<Integer, Integer>> buildQuoteLookup(List<Transporter> transporters) {
        Map<Integer, Map<Integer, Integer>> lookup = new HashMap<>();
        for (Transporter t : transporters) {
            Map<Integer, Integer> laneToQuote = new HashMap<>();
            for (LaneQuote lq : t.getLaneQuotes()) {
                laneToQuote.put(lq.getLaneId(), lq.getQuote());
            }
            lookup.put(t.getId(), laneToQuote);
        }
        return lookup;
    }

    private AssignmentResult tryAssignment(List<Lane> lanes, List<Integer> combo,
                                           Map<Integer, Map<Integer, Integer>> quoteLookup) {
        List<AssignmentDetail> assignments = new ArrayList<>();
        long totalCost = 0;

        for (Lane lane : lanes) {
            int bestQuote = Integer.MAX_VALUE;
            int bestTransporterId = -1;

            for (int tId : combo) {
                Map<Integer, Integer> quotes = quoteLookup.get(tId);
                if (quotes != null && quotes.containsKey(lane.getId())) {
                    int quote = quotes.get(lane.getId());
                    if (quote < bestQuote) {
                        bestQuote = quote;
                        bestTransporterId = tId;
                    }
                }
            }

            if (bestTransporterId == -1) {
                return null;
            }

            assignments.add(new AssignmentDetail(lane.getId(), bestTransporterId));
            totalCost += bestQuote;
        }

        return new AssignmentResult(totalCost, assignments);
    }

    private List<List<Integer>> generateCombinations(List<Integer> items, int size) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(items, size, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Integer> items, int size, int start,
                           List<Integer> current, List<List<Integer>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }

        int remaining = items.size() - start;
        int needed = size - current.size();
        if (remaining < needed) {
            return;
        }

        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            backtrack(items, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static class AssignmentResult {
        final long totalCost;
        final List<AssignmentDetail> assignments;

        AssignmentResult(long totalCost, List<AssignmentDetail> assignments) {
            this.totalCost = totalCost;
            this.assignments = assignments;
        }
    }
}
