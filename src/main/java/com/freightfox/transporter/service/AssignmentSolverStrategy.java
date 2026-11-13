package com.freightfox.transporter.service;

import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.Transporter;

import java.util.List;

/**
 * Strategy interface for solving transporter-to-lane assignment optimization problems.
 * Decouples the optimization algorithm from orchestration services, allowing alternative
 * algorithms (e.g. Linear Programming, Hungarian Algorithm, Genetic Algorithms) to be
 * plugged in without changing service code.
 */
public interface AssignmentSolverStrategy {

    /**
     * Finds the optimal assignment of transporters to lanes.
     *
     * @param lanes            list of trade lanes to cover
     * @param transporters     list of available transporters with quotes
     * @param maxTransporters  maximum allowed transporters
     * @return AssignmentResponse containing itemized assignments and total cost
     */
    AssignmentResponse solve(List<Lane> lanes, List<Transporter> transporters, int maxTransporters);
}

