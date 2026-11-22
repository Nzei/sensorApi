package com.uche.sensorApi.service;

import com.uche.sensorApi.model.CreateReadingRequest;
import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.repository.ReadingRepository;
import org.springframework.stereotype.Service;

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
}
