package com.uche.sensorApi.controller;

import com.uche.sensorApi.model.CreateReadingRequest;
import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/readings")
@RestController
public class ReadingController {
    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping
    public ResponseEntity<Reading> registerReading(@Valid @RequestBody CreateReadingRequest createReadingRequest) {
        Reading savedReading = readingService.registerReading(createReadingRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReading);
    }
}
