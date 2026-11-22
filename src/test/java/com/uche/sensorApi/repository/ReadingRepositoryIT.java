package com.uche.sensorApi.repository;

import com.uche.sensorApi.model.Reading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class ReadingRepositoryIT {

    @Autowired
    private ReadingRepository readingRepository;

    @BeforeEach
    void setUp(){
        readingRepository.save(new Reading("ReadingSensor2", 50.0, Instant.parse("2026-11-22T17:09:00Z")));
        readingRepository.save(new Reading("ReadingSensor3", 70.0, Instant.parse("2026-11-22T18:09:00Z")));
        readingRepository.save(new Reading("ReadingSensor2", 60.0, Instant.parse("2026-11-22T19:09:00Z")));
        readingRepository.save(new Reading("ReadingSensor3", 100.0, Instant.parse("2026-11-23T17:09:00Z")));
    }

    @Test
    void shouldCalculateAverageForAllSensors(){
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-23T00:00:00Z");

        Double averageReading = readingRepository.averageTemperature(fromDate, toDate);
        assertEquals(60.0, averageReading);
    }

    @Test
    void shouldReturnNullWhenNoReadingsMatch(){
        Instant fromDate = Instant.parse("2027-01-01T00:00:00Z");
        Instant toDate = Instant.parse("2027-01-02T00:00:00Z");

        Double average = readingRepository.averageTemperature(fromDate, toDate);

        assertNull(average);
    }

    @Test
    void shouldCalculateAverageForSpecificSensor(){
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-23T00:00:00Z");

        Double average = readingRepository.averageTemperatureBySensor("ReadingSensor2", fromDate, toDate);
        assertEquals(55.0, average);
    }

    @Test
    void shouldExcludeEndBoundary() {
        Instant fromDate = Instant.parse("2026-11-22T00:00:00Z");
        Instant toDate = Instant.parse("2026-11-22T18:09:00Z");

        Double average = readingRepository.averageTemperature(fromDate, toDate);
        assertEquals(50.0, average);
    }
}
