package com.springai.api_monitoring.repository;

import com.springai.api_monitoring.model.Anomalies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnomaliesRepository extends JpaRepository<Anomalies, Long> {
    Optional<Anomalies> findByMetricIdAndType(Long metricId, String type);
}
