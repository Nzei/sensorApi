package com.uche.sensorApi.service;

import com.uche.sensorApi.model.CreateReadingRequest;
import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.repository.ReadingRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReadingService {
    private final ReadingRepository readingRepository;

    public ReadingService(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    public Reading registerReading(CreateReadingRequest createReadingRequest) {
        Reading reading = new Reading(createReadingRequest.sensorId(), createReadingRequest.temperature(), createReadingRequest.timestamp());
        return readingRepository.save(reading);
    }

    public Double getAverageTemperature(String sensorId, Instant fromDate, Instant toDate){
        if (!fromDate.isBefore(toDate)){
            throw new IllegalArgumentException("fromDate must be before toDate");
        }
        if (sensorId == null){
            return readingRepository.averageTemperature(fromDate, toDate);
        }
        if (sensorId.isBlank()){
            throw new IllegalArgumentException("sensorId can not be blank");
        }
        return readingRepository.averageTemperatureBySensor(sensorId, fromDate, toDate);
    }
}
