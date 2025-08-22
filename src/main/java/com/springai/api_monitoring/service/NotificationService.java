package com.springai.api_monitoring.service;

import com.springai.api_monitoring.model.Anomalies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void notifyAnomaly(Anomalies anomaly) {
        // For now, just print an alert (could later trigger email, Slack, etc.)
        logger.warn("ALERT: Anomaly detected! API: {}, Type: {}, Severity: {}, DetectedAt: {}",
                anomaly.getApi().getName(),
                anomaly.getType(),
                anomaly.getSeverity(),
                anomaly.getDetectedAt());
    }
}
