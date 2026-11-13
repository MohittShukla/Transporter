package com.freightfox.transporter.service;

import com.freightfox.transporter.dto.request.InputRequest;
import com.freightfox.transporter.dto.request.LaneDto;
import com.freightfox.transporter.dto.request.LaneQuoteDto;
import com.freightfox.transporter.dto.request.TransporterDto;
import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.dto.response.InputResponse;
import com.freightfox.transporter.exception.NoDataException;
import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.LaneQuote;
import com.freightfox.transporter.model.Transporter;
import com.freightfox.transporter.store.InMemoryDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating data persistence and assignment optimization.
 * Uses {@link AssignmentSolverStrategy} following the Strategy Pattern.
 */
@Service
public class TransporterService {

    private static final Logger log = LoggerFactory.getLogger(TransporterService.class);

    private final InMemoryDataStore dataStore;
    private final AssignmentSolverStrategy solver;

    public TransporterService(InMemoryDataStore dataStore, AssignmentSolverStrategy solver) {
        this.dataStore = dataStore;
        this.solver = solver;
    }

    /**
     * Processes and stores input lanes and transporter quotes.
     */
    public InputResponse saveInput(InputRequest request) {
        log.info("Processing input data: {} lanes, {} transporters",
                request.getLanes().size(), request.getTransporters().size());

        List<Lane> lanes = request.getLanes().stream()
                .map(this::toLane)
                .collect(Collectors.toList());

        List<Transporter> transporters = request.getTransporters().stream()
                .map(this::toTransporter)
                .collect(Collectors.toList());

        dataStore.saveData(lanes, transporters);
        log.info("Successfully persisted {} lanes and {} transporters in store", lanes.size(), transporters.size());

        return new InputResponse("success", "Input data saved successfully.");
    }

    /**
     * Solves optimal assignments based on stored data and user limit.
     */
    public AssignmentResponse getAssignment(int maxTransporters) {
        if (!dataStore.hasData()) {
            log.warn("Assignment requested before data ingestion");
            throw new NoDataException("No input data available. Submit data via /api/v1/transporters/input first.");
        }

        log.info("Orchestrating assignment optimization with maxTransporters={}", maxTransporters);
        return solver.solve(dataStore.getLanes(), dataStore.getTransporters(), maxTransporters);
    }

    private Lane toLane(LaneDto dto) {
        return new Lane(dto.getId(), dto.getOrigin(), dto.getDestination());
    }

    private Transporter toTransporter(TransporterDto dto) {
        List<LaneQuote> quotes = dto.getLaneQuotes().stream()
                .map(q -> new LaneQuote(q.getLaneId(), q.getQuote()))
                .collect(Collectors.toList());
        return new Transporter(dto.getId(), dto.getName(), quotes);
    }
}
