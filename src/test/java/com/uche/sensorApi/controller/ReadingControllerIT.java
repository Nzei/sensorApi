package com.uche.sensorApi.controller;

import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.repository.ReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ReadingControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReadingRepository readingRepository;

    @BeforeEach
    void setUp() {
        readingRepository.deleteAll();
    }

    @Test
    void shouldRegisterReadingSuccessfully() throws Exception{
        String request = """
                {
                    "sensorId": "readingSensor1",
                    "temperature": 10,
                    "timestamp": "2026-11-22T15:14:00Z"
                }
                """;
        mockMvc.perform(post("/api/v1/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sensorId").value("readingSensor1"))
                .andExpect(jsonPath("$.temperature").value(10))
                .andExpect(jsonPath("$.timestamp").value("2026-11-22T15:14:00Z"));

        assertEquals(1, readingRepository.count());
    }

    @Test
    void shouldRejectMissingSensorId() throws Exception {
        String request = """
                {
                    "temperature": 100.0,
                    "timestamp": "2026-11-22T15:14:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(0, readingRepository.count());
    }

    @Test
    void shouldRejectMissingTimestamp() throws Exception {
        String request = """
                {
                    "sensorId": "readingSensor1",
                    "temperature": 67.9
                }
                """;

        mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(0, readingRepository.count());
    }

    @Test
    void shouldRejectInvalidTimestamp() throws Exception {
        String request = """
                {
                    "sensorId": "readingSensor1",
                    "temperature": 88.2,
                    "timestamp": "no date"
                }
                """;

        mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(0, readingRepository.count());
    }

    @Test
    void shouldRejectMissingTemperature() throws Exception {
        String request = """
                {
                    "sensorId": "readingSensor1",
                    "timestamp": "2026-11-22T15:14:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
        assertEquals(0, readingRepository.count());
    }

    @Test
    void shouldRejectNonFiniteTemperature() throws Exception {
        String request = """
                {
                    "sensorId": "readingSensor1",
                    "temperature": "NaN",
                    "timestamp": "2026-11-22T15:14:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
        assertEquals(0, readingRepository.count());
    }

    @Test
    void shouldCalculateAverageForAllSensors() throws Exception {
        readingRepository.save(new Reading("readingSensor1", 50.0, Instant.parse("2026-11-22T15:14:00Z")));
        readingRepository.save(new Reading("readingSensor2", 70.0, Instant.parse("2026-11-22T16:14:00Z")));
        readingRepository.save(new Reading("readingSensor1", 60.0, Instant.parse("2026-11-22T17:14:00Z")));

        mockMvc.perform(get("/api/v1/readings/average")
                        .param("fromDate", "2026-11-22T00:00:00Z")
                        .param("toDate", "2026-11-23T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageReading").value(60.0));
    }

    @Test
    void shouldCalculateAverageForASpecificSensor() throws Exception {
        readingRepository.save(new Reading("readingSensor1", 50.0, Instant.parse("2026-11-22T15:14:00Z")));
        readingRepository.save(new Reading("readingSensor2", 70.0, Instant.parse("2026-11-22T16:14:00Z")));
        readingRepository.save(new Reading("readingSensor1", 60.0, Instant.parse("2026-11-22T17:14:00Z")));

        mockMvc.perform(get("/api/v1/readings/average")
                        .param("sensorId", "readingSensor1")
                        .param("fromDate", "2026-11-22T00:00:00Z")
                        .param("toDate", "2026-11-23T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageReading").value(55.0));
    }

    @Test
    void shouldReturnNoContentWhenNoMatchForReadings() throws Exception {
        mockMvc.perform(get("/api/v1/readings/average")
                        .param("fromDate", "2027-01-01T00:00:00Z")
                        .param("toDate", "2027-01-02T00:00:00Z"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/v1/readings/average")
                        .param("fromDate", "2026-11-23T00:00:00Z")
                        .param("toDate", "2026-11-22T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("fromDate must be before toDate"));
    }

    @Test
    void shouldRejectEqualDateRange() throws Exception {
        mockMvc.perform(get("/api/v1/readings/average")
                        .param("fromDate", "2026-11-22T00:00:00Z")
                        .param("toDate", "2026-11-22T00:00:00Z"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankSensorFilter() throws Exception {
        mockMvc.perform(get("/api/v1/readings/average")
                        .param("sensorId", " ")
                        .param("fromDate", "2026-11-22T00:00:00Z")
                        .param("toDate", "2026-11-23T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("sensorId can not be blank"));
    }

    @Test
    void shouldRejectInvalidDateParameter() throws Exception {
        mockMvc.perform(get("/api/v1/readings/average")
                        .param("fromDate", "no date")
                        .param("toDate", "2026-11-23T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
