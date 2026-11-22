package com.uche.sensorApi.repository;

import com.uche.sensorApi.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
}
