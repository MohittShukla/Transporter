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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating data persistence and assignment optimization.
 */
@Service
public class TransporterService {

    private final InMemoryDataStore dataStore;
    private final AssignmentSolver solver;

    public TransporterService(InMemoryDataStore dataStore, AssignmentSolver solver) {
        this.dataStore = dataStore;
        this.solver = solver;
    }

    /**
     * Processes and stores input lanes and transporter quotes.
     */
    public InputResponse saveInput(InputRequest request) {
        List<Lane> lanes = request.getLanes().stream()
                .map(this::toLane)
                .collect(Collectors.toList());

        List<Transporter> transporters = request.getTransporters().stream()
                .map(this::toTransporter)
                .collect(Collectors.toList());

        dataStore.saveData(lanes, transporters);

        return new InputResponse("success", "Input data saved successfully.");
    }

    /**
     * Solves optimal assignments based on stored data and user limit.
     */
    public AssignmentResponse getAssignment(int maxTransporters) {
        if (!dataStore.hasData()) {
            throw new NoDataException("No input data available. Submit data via /api/v1/transporters/input first.");
        }

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
