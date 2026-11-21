package com.interestcalculator.controller;

import com.interestcalculator.service.CompoundInterestService;
import com.interestcalculator.service.SimpleInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// These tests act like the web page: they SEND a request to the app (without opening
// a browser or a real port) and check the status code and the reply.
//
// @WebMvcTest starts only the web part of the app, which is quick.
// @Import adds the two services, because the controller needs them.
@WebMvcTest(CalculatorController.class)
@Import({SimpleInterestService.class, CompoundInterestService.class})
class CalculatorControllerTest {

    // MockMvc is the pretend web page that sends requests to the controller
    @Autowired
    private MockMvc mockMvc;

    @Test
    void simpleRequestReturnsInterestAndAmount() throws Exception {
        String body = """
                { "find": "AMOUNT", "principal": 8000, "rate": 7, "time": 2, "unit": 1 }
                """;

        mockMvc.perform(post("/api/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interest", closeTo(1120.0, 0.01)))
                .andExpect(jsonPath("$.amount", closeTo(9120.0, 0.01)));
    }

    @Test
    void compoundRequestReturnsAmountAndInterest() throws Exception {
        String body = """
                { "find": "AMOUNT", "principal": 10000, "rate": 10, "time": 180,
                  "unit": 4, "timesPerYear": 4 }
                """;

        mockMvc.perform(post("/api/compound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", closeTo(10506.25, 0.01)))
                .andExpect(jsonPath("$.interest", closeTo(506.25, 0.01)));
    }

    @Test
    void missingRateGivesStatus400AndAMessage() throws Exception {
        String body = """
                { "find": "AMOUNT", "principal": 8000, "time": 2, "unit": 1 }
                """;

        mockMvc.perform(post("/api/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("Please enter the rate")));
    }

    @Test
    void rateOfZeroGivesStatus400() throws Exception {
        String body = """
                { "find": "AMOUNT", "principal": 8000, "rate": 0, "time": 2, "unit": 1 }
                """;

        mockMvc.perform(post("/api/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("Rate must be greater than 0")));
    }

    @Test
    void aMadeUpFindValueGivesStatus400() throws Exception {
        String body = """
                { "find": "BANANA", "principal": 8000, "rate": 7, "time": 2, "unit": 1 }
                """;

        mockMvc.perform(post("/api/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }
}
