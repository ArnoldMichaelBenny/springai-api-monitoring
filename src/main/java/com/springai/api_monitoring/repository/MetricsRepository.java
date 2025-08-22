package com.springai.api_monitoring.repository;

import com.springai.api_monitoring.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    // This method enables efficient, time-based querying for the anomaly detection service.
    List<Metrics> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
}