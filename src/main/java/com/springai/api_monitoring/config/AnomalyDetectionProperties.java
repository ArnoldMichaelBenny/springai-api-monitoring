package com.springai.api_monitoring.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "anomaly.detection")
@Validated
public class AnomalyDetectionProperties {

    @NotNull
    private final Thresholds thresholds = new Thresholds();

    public Thresholds getThresholds() {
        return thresholds;
    }

    public static class Thresholds {
        @NotNull
        private Double errorRate = 0.1;

        @NotNull
        private Double responseTime = 800.0;

        public Double getErrorRate() {
            return errorRate;
        }

        public void setErrorRate(Double errorRate) {
            this.errorRate = errorRate;
        }

        public Double getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(Double responseTime) {
            this.responseTime = responseTime;
        }
    }
}