package com.freightfox.transporter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.transporter.dto.request.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests for Transporter REST endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransporterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INPUT_URL = "/api/v1/transporters/input";
    private static final String ASSIGNMENT_URL = "/api/v1/transporters/assignment";

    // ── API 1 Tests ──

    @Test
    @Order(1)
    @DisplayName("API 1: valid input returns 200 success")
    void submitInput_valid() throws Exception {
        InputRequest request = buildTestCase1Input();

        mockMvc.perform(post(INPUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Input data saved successfully."));
    }

    @Test
    @Order(2)
    @DisplayName("API 1: empty lanes returns 400")
    void submitInput_emptyLanes() throws Exception {
        InputRequest request = new InputRequest(
                Collections.emptyList(),
                List.of(new TransporterDto(1, "T1",
                        List.of(new LaneQuoteDto(1, 1000))))
        );

        mockMvc.perform(post(INPUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @Order(3)
    @DisplayName("API 1: null body returns 400")
    void submitInput_nullBody() throws Exception {
        mockMvc.perform(post(INPUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ── API 2 Tests ──

    @Test
    @Order(4)
    @DisplayName("API 2: returns optimized assignment after valid input")
    void getAssignment_afterValidInput() throws Exception {
        InputRequest input = buildTestCase1Input();
        mockMvc.perform(post(INPUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());

        AssignmentRequest assignmentReq = new AssignmentRequest(3);
        mockMvc.perform(post(ASSIGNMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalCost").isNumber())
                .andExpect(jsonPath("$.assignments", hasSize(5)))
                .andExpect(jsonPath("$.selectedTransporters", hasSize(lessThanOrEqualTo(3))));
    }

    @Test
    @Order(5)
    @DisplayName("API 2: missing maxTransporters returns 400")
    void getAssignment_missingMax() throws Exception {
        mockMvc.perform(post(ASSIGNMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @Order(6)
    @DisplayName("API 2: maxTransporters=0 returns 400")
    void getAssignment_zeroMax() throws Exception {
        AssignmentRequest request = new AssignmentRequest(0);
        mockMvc.perform(post(ASSIGNMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── Test Case 2: sparse quotes ──

    @Test
    @Order(7)
    @DisplayName("Test Case 2: sparse quotes full integration")
    void testCase2_sparseQuotes() throws Exception {
        InputRequest input = buildTestCase2Input();
        mockMvc.perform(post(INPUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());

        AssignmentRequest assignmentReq = new AssignmentRequest(3);
        mockMvc.perform(post(ASSIGNMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.assignments", hasSize(5)))
                .andExpect(jsonPath("$.selectedTransporters", hasSize(lessThanOrEqualTo(3))));
    }

    // ── Helper: build test data ──

    private InputRequest buildTestCase1Input() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1, "Mumbai", "Delhi"),
                new LaneDto(2, "Delhi", "Bangalore"),
                new LaneDto(3, "Chennai", "Kolkata"),
                new LaneDto(4, "Pune", "Hyderabad"),
                new LaneDto(5, "Ahmedabad", "Jaipur")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1, "T1", List.of(
                        new LaneQuoteDto(1, 20835), new LaneQuoteDto(2, 10512),
                        new LaneQuoteDto(3, 22105), new LaneQuoteDto(4, 42481), new LaneQuoteDto(5, 19862))),
                new TransporterDto(2, "T2", List.of(
                        new LaneQuoteDto(1, 48844), new LaneQuoteDto(2, 31326),
                        new LaneQuoteDto(3, 18640), new LaneQuoteDto(4, 45828), new LaneQuoteDto(5, 18297))),
                new TransporterDto(3, "T3", List.of(
                        new LaneQuoteDto(1, 39020), new LaneQuoteDto(2, 20648),
                        new LaneQuoteDto(3, 31438), new LaneQuoteDto(4, 36447), new LaneQuoteDto(5, 12789))),
                new TransporterDto(4, "T4", List.of(
                        new LaneQuoteDto(1, 14400), new LaneQuoteDto(2, 44514),
                        new LaneQuoteDto(3, 14316), new LaneQuoteDto(4, 10678), new LaneQuoteDto(5, 13032))),
                new TransporterDto(5, "T5", List.of(
                        new LaneQuoteDto(1, 11601), new LaneQuoteDto(2, 19760),
                        new LaneQuoteDto(3, 40870), new LaneQuoteDto(4, 20635), new LaneQuoteDto(5, 26421))),
                new TransporterDto(6, "T6", List.of(
                        new LaneQuoteDto(1, 35095), new LaneQuoteDto(2, 12494),
                        new LaneQuoteDto(3, 17808), new LaneQuoteDto(4, 36210), new LaneQuoteDto(5, 39444))),
                new TransporterDto(7, "T7", List.of(
                        new LaneQuoteDto(1, 26070), new LaneQuoteDto(2, 41098),
                        new LaneQuoteDto(3, 20932), new LaneQuoteDto(4, 16897), new LaneQuoteDto(5, 27938)))
        );

        return new InputRequest(lanes, transporters);
    }

    private InputRequest buildTestCase2Input() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1, "Chandigarh", "Shimla"),
                new LaneDto(2, "Agra", "Kanpur"),
                new LaneDto(3, "Varanasi", "Gorakhpur"),
                new LaneDto(4, "Amritsar", "Ludhiana"),
                new LaneDto(5, "Coimbatore", "Madurai")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1, "X", List.of(new LaneQuoteDto(1, 1000), new LaneQuoteDto(2, 1500))),
                new TransporterDto(2, "Y", List.of(new LaneQuoteDto(3, 2000), new LaneQuoteDto(4, 1800))),
                new TransporterDto(3, "Z", List.of(new LaneQuoteDto(5, 1200))),
                new TransporterDto(4, "W", List.of(new LaneQuoteDto(1, 1100), new LaneQuoteDto(4, 1750)))
        );

        return new InputRequest(lanes, transporters);
    }
}
