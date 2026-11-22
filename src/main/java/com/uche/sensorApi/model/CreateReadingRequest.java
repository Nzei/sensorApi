package com.uche.sensorApi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateReadingRequest (@NotBlank String sensorId, @NotNull Double temperature, @NotNull Instant timestamp) {}
