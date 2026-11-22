package com.uche.sensorApi.controller;

import com.uche.sensorApi.repository.ReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
