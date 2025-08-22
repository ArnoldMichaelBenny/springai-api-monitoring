package com.springai.api_monitoring.service;

import com.springai.api_monitoring.model.Metrics;
import com.springai.api_monitoring.repository.MetricsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetricsService {
    private final MetricsRepository metricsRepository;

    public MetricsService(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public List<Metrics> getAllMetrics() {
        return metricsRepository.findAll();
    }

    public Optional<Metrics> getMetricById(Long id) {
        return metricsRepository.findById(id);
    }

    public Metrics saveMetric(Metrics metric) {
        return metricsRepository.save(metric);
    }

    public Metrics updateMetric(Long id, Metrics metricDetails) {
        Metrics metric = metricsRepository.findById(id).orElseThrow();
        metric.setApi(metricDetails.getApi());
        metric.setResponseTime(metricDetails.getResponseTime());
        metric.setErrorRate(metricDetails.getErrorRate());
        metric.setThroughput(metricDetails.getThroughput());
        metric.setTimestamp(metricDetails.getTimestamp());
        return metricsRepository.save(metric);
    }

    public void deleteMetric(Long id) {
        metricsRepository.deleteById(id);
    }
}
