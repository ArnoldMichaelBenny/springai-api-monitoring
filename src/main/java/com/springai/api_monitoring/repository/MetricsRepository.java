package com.springai.api_monitoring.repository;

import com.springai.api_monitoring.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {}
