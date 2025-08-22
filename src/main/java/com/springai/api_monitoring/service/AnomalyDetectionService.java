package com.springai.api_monitoring.service;

import com.springai.api_monitoring.config.AnomalyDetectionProperties;
import com.springai.api_monitoring.model.Severity;
import com.springai.api_monitoring.model.Anomalies;
import com.springai.api_monitoring.model.Metrics;
import com.springai.api_monitoring.repository.AnomaliesRepository;
import com.springai.api_monitoring.repository.MetricsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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

    // Main detection logic, manual or scheduled
    public int detectAnomalies() {
        List<Metrics> metricsList = metricsRepository.findAll();
        int newAnomaliesCount = 0;
        for (Metrics metric : metricsList) {
            // Refactored to a helper method for clarity
            detectAndRecordAnomalyForMetric(metric).ifPresent(anomaly -> newAnomaliesCount++);
        }
        return newAnomaliesCount;
    }

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

    // Automatically run detection every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void scheduledDetection() {
        int count = detectAnomalies();
        if (count > 0) {
            // Use parameterized logging for better performance and readability
            logger.info("{} new anomalies detected automatically at {}", count, LocalDateTime.now());
        }
    }
}
