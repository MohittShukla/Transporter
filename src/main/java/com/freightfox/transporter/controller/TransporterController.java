package com.freightfox.transporter.controller;

import com.freightfox.transporter.dto.request.AssignmentRequest;
import com.freightfox.transporter.dto.request.InputRequest;
import com.freightfox.transporter.dto.response.AssignmentResponse;
import com.freightfox.transporter.dto.response.InputResponse;
import com.freightfox.transporter.service.TransporterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints for data ingestion and assignment optimization.
 */
@RestController
@RequestMapping("/api/v1/transporters")
@Tag(name = "Transporter Assignment", description = "APIs for transporter-to-lane assignment optimization")
public class TransporterController {

    private static final Logger log = LoggerFactory.getLogger(TransporterController.class);

    private final TransporterService service;

    public TransporterController(TransporterService service) {
        this.service = service;
    }

    /**
     * Ingests lane and transporter quote data.
     */
    @PostMapping("/input")
    @Operation(summary = "Submit input data", description = "Submit lanes and transporter quotes for optimization")
    public ResponseEntity<InputResponse> submitInput(@Valid @RequestBody InputRequest request) {
        log.info("Received POST /api/v1/transporters/input with {} lanes and {} transporters",
                request.getLanes() != null ? request.getLanes().size() : 0,
                request.getTransporters() != null ? request.getTransporters().size() : 0);
        InputResponse response = service.saveInput(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Solves and returns optimal transporter-to-lane assignments respecting maxTransporters constraint.
     */
    @PostMapping("/assignment")
    @Operation(summary = "Get optimized assignments", description = "Returns optimal transporter-to-lane assignments within the specified transporter limit")
    public ResponseEntity<AssignmentResponse> getAssignment(@Valid @RequestBody AssignmentRequest request) {
        log.info("Received POST /api/v1/transporters/assignment with maxTransporters={}", request.getMaxTransporters());
        AssignmentResponse response = service.getAssignment(request.getMaxTransporters());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
