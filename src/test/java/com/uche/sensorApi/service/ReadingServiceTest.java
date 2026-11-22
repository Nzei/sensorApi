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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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


    @Test
    void shouldRejectNonFiniteTemperature() {
        CreateReadingRequest request = new CreateReadingRequest("readingSensor1", Double.NaN, Instant.parse("2026-11-22T15:14:00Z"));
        assertThrows(IllegalArgumentException.class, () -> readingService.registerReading(request));
        verify(readingRepository, never()).save(any(Reading.class));
    }

    @Test
    void shouldCalculateAverageForAllSensors() {
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-23T00:00:00Z");

        when(readingRepository.averageTemperature(fromDate, toDate)).thenReturn(60.0);

        Double average = readingService.getAverageTemperature(null, fromDate, toDate);
        assertEquals(60.0, average);
        verify(readingRepository).averageTemperature(fromDate, toDate);
    }

    @Test
    void shouldCalculateAverageForSpecificSensor() {
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-23T00:00:00Z");

        when(readingRepository.averageTemperatureBySensor("readingSensor1", fromDate, toDate)).thenReturn(55.0);

        Double average = readingService.getAverageTemperature("readingSensor1", fromDate, toDate);
        assertEquals(55.0, average);
        verify(readingRepository).averageTemperatureBySensor("readingSensor1", fromDate, toDate);
    }

    @Test
    void shouldRejectInvalidDateRange() {
        Instant fromDate = Instant.parse("2026-11-23T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-22T00:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> readingService.getAverageTemperature(null, fromDate, toDate));
    }

    @Test
    void shouldRejectEqualDateRange() {
        Instant date = Instant.parse("2026-11-22T00:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> readingService.getAverageTemperature(null, date, date));
    }

    @Test
    void shouldRejectBlankSensorId() {
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-23T00:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> readingService.getAverageTemperature(" ", fromDate, toDate));
    }

}
