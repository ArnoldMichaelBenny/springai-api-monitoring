package com.springai.api_monitoring.service;

import com.springai.api_monitoring.config.AnomalyDetectionProperties;
import com.springai.api_monitoring.model.Severity;
import com.springai.api_monitoring.model.Anomalies;
import com.springai.api_monitoring.model.Metrics;
import com.springai.api_monitoring.repository.AnomaliesRepository;
import com.springai.api_monitoring.repository.MetricsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnomalyDetectionService {

    private final MetricsRepository metricsRepository;
    private final AnomaliesRepository anomaliesRepository;
    private final NotificationService notificationService;
    private final AnomalyDetectionProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionService.class);

    public AnomalyDetectionService(
            MetricsRepository metricsRepository,
            AnomaliesRepository anomaliesRepository,
            NotificationService notificationService,
            AnomalyDetectionProperties properties) {
        this.metricsRepository = metricsRepository;
        this.anomaliesRepository = anomaliesRepository;
        this.notificationService = notificationService;
        this.properties = properties;
    }

    // Main detection logic, processes metrics within a given time window
    public int detectAnomalies(LocalDateTime startTime, LocalDateTime endTime) {
        List<Metrics> metricsList = metricsRepository.findByTimestampBetween(startTime, endTime);
        // Use a stream to process metrics and count the successfully created anomalies.
        // This avoids the "effectively final" lambda issue and is more idiomatic.
        return (int) metricsList.stream()
                .map(this::detectAndRecordAnomalyForMetric)
                .filter(Optional::isPresent)
                .count();
    }

    @Transactional
    private Optional<Anomalies> detectAndRecordAnomalyForMetric(Metrics metric) {
        List<String> anomalyTypes = new ArrayList<>();
        Severity severity = null;

        if (metric.getErrorRate() != null && metric.getErrorRate() > properties.getThresholds().getErrorRate()) {
            anomalyTypes.add("High Error Rate");
            severity = Severity.CRITICAL;
        }

        if (metric.getResponseTime() != null && metric.getResponseTime() > properties.getThresholds().getResponseTime()) {
            anomalyTypes.add("Slow Response Time");
            // Set severity to WARNING only if a more critical one hasn't been set
            if (severity == null) {
                severity = Severity.WARNING;
            }
        }

        if (anomalyTypes.isEmpty()) {
            return Optional.empty();
        }

        String typeString = String.join("; ", anomalyTypes);

        // Deduplication: only insert if not already present for this metric and type
        if (anomaliesRepository.findByMetricIdAndType(metric.getId(), typeString).isPresent()) {
            return Optional.empty();
        }

        Anomalies anomaly = Anomalies.builder()
                .api(metric.getApi())
                .metric(metric)
                .type(typeString)
                .severity(severity.name())
                .detectedAt(LocalDateTime.now())
                .build();

        anomaliesRepository.save(anomaly);
        notificationService.notifyAnomaly(anomaly);

        return Optional.of(anomaly);
    }

    // Automatically run detection every 5 minutes.
    // Using fixedDelay to ensure there's a 5-minute pause *after* the last execution completes.
    @Scheduled(fixedDelay = 300000)
    public void scheduledDetection() {
        LocalDateTime endTime = LocalDateTime.now();
        // Check metrics from the last 5 minutes to align with the schedule
        LocalDateTime startTime = endTime.minusMinutes(5);
        int count = detectAnomalies(startTime, endTime);
        if (count > 0) {
            // Use parameterized logging for better performance and readability
            logger.info("{} new anomalies detected automatically in time window {} to {}", count, startTime, endTime);
        }
    }
}
