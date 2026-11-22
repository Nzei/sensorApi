package com.uche.sensorApi.controller;

import com.uche.sensorApi.model.AverageResponse;
import com.uche.sensorApi.model.CreateReadingRequest;
import com.uche.sensorApi.model.Reading;
import com.uche.sensorApi.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

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

    @GetMapping("/average")
    public ResponseEntity<AverageResponse> getAverageTemperature(@RequestParam(required = false) String sensorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate){
        Double averageReading = readingService.getAverageTemperature(sensorId, fromDate, toDate);
        if (averageReading == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new AverageResponse(averageReading));
    }
}
