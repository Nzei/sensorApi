package com.uche.sensorApi.repository;

import com.uche.sensorApi.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    @Query("""
select avg(r.temperature)
from Reading r
where r.timestamp >= :fromDate
and r.timestamp < :toDate
""")
    Double averageTemperature(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("""
select avg(r.temperature)
from Reading r
where r.sensorId = :sensorId
and r.timestamp >= :fromDate
and r.timestamp < :toDate
""")
    Double averageTemperatureBySensor(@Param("sensorId") String sensorId, @Param("fromDate") Instant from, @Param("toDate") Instant toDate);
}
