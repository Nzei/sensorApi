package com.uche.sensorApi.service;

import com.uche.sensorApi.model.CreateReadingRequest;
import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.repository.ReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadingServiceTest {
    @Mock
    private ReadingRepository readingRepository;

    @InjectMocks
    private ReadingService readingService;

    private CreateReadingRequest createReadingRequest;

    @BeforeEach
    void setUp() {
        createReadingRequest = new CreateReadingRequest("readingSensor1", 10.0, Instant.parse("2026-11-22T15:14:00Z"));
    }

    @Test
    void shouldRegisterReading(){
        when(readingRepository.save(any(Reading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reading readingResult = readingService.registerReading(createReadingRequest);

        assertEquals("readingSensor1", readingResult.getSensorId());
        assertEquals(10.0, readingResult.getTemperature());
        assertEquals(Instant.parse("2026-11-22T15:14:00Z"), readingResult.getTimestamp());

        verify(readingRepository).save(any(Reading.class));
    }

    @Test
    void shouldReturnSavedReading() {
        Reading savedReading = new Reading(createReadingRequest.sensorId(), createReadingRequest.temperature(), createReadingRequest.timestamp());

        when(readingRepository.save(any(Reading.class))).thenReturn(savedReading);
        Reading readingResult = readingService.registerReading(createReadingRequest);
        assertSame(savedReading, readingResult);
    }
}
