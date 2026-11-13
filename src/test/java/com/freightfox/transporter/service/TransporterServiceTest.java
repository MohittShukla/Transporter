package com.freightfox.transporter.service;

import com.freightfox.transporter.dto.request.InputRequest;
import com.freightfox.transporter.dto.request.LaneDto;
import com.freightfox.transporter.dto.request.LaneQuoteDto;
import com.freightfox.transporter.dto.request.TransporterDto;
import com.freightfox.transporter.dto.response.AssignmentDetail;
import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.dto.response.InputResponse;
import com.freightfox.transporter.exception.NoDataException;
import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.Transporter;
import com.freightfox.transporter.store.InMemoryDataStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Isolated unit tests for TransporterService verifying orchestration and error handling.
 */
@ExtendWith(MockitoExtension.class)
class TransporterServiceTest {

    @Mock
    private InMemoryDataStore dataStore;

    @Mock
    private AssignmentSolverStrategy solver;

    @InjectMocks
    private TransporterService service;

    @Test
    @DisplayName("saveInput: maps DTOs to domain entities and persists to store")
    void saveInput_success() {
        InputRequest request = new InputRequest(
                List.of(new LaneDto(1, "Mumbai", "Delhi")),
                List.of(new TransporterDto(1, "T1", List.of(new LaneQuoteDto(1, 5000))))
        );

        InputResponse response = service.saveInput(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Input data saved successfully.", response.getMessage());

        verify(dataStore, times(1)).saveData(anyList(), anyList());
    }

    @Test
    @DisplayName("getAssignment: throws NoDataException when store has no data")
    void getAssignment_throwsWhenNoData() {
        when(dataStore.hasData()).thenReturn(false);

        NoDataException exception = assertThrows(NoDataException.class,
                () -> service.getAssignment(3));

        assertTrue(exception.getMessage().contains("No input data available"));
        verify(solver, never()).solve(anyList(), anyList(), anyInt());
    }

    @Test
    @DisplayName("getAssignment: delegates to solver when store has data")
    void getAssignment_delegatesToSolver() {
        List<Lane> lanes = List.of(new Lane(1, "Mumbai", "Delhi"));
        List<Transporter> transporters = List.of(new Transporter(1, "T1", List.of()));
        AssignmentResponse expectedResponse = new AssignmentResponse(
                "success", 5000,
                List.of(new AssignmentDetail(1, 1)),
                List.of(1)
        );

        when(dataStore.hasData()).thenReturn(true);
        when(dataStore.getLanes()).thenReturn(lanes);
        when(dataStore.getTransporters()).thenReturn(transporters);
        when(solver.solve(lanes, transporters, 2)).thenReturn(expectedResponse);

        AssignmentResponse actualResponse = service.getAssignment(2);

        assertNotNull(actualResponse);
        assertEquals(5000, actualResponse.getTotalCost());
        assertEquals(1, actualResponse.getSelectedTransporters().size());
        verify(solver, times(1)).solve(lanes, transporters, 2);
    }
}

