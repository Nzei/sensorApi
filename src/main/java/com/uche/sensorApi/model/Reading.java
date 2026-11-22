package com.uche.sensorApi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorId;
    private Double temperature;
    private Instant timestamp;

    protected Reading(){}

    public Reading(String sensorId, Double temperature, Instant timestamp) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
