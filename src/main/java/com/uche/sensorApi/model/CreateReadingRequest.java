package com.uche.sensorApi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateReadingRequest (@NotBlank String sensorId, @NotNull BigDecimal temperature, @NotNull Instant timestamp) {}
