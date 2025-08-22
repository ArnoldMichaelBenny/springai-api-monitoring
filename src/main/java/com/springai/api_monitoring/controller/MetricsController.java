package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.Metrics;
import com.springai.api_monitoring.service.MetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    public List<Metrics> getAllMetrics() {
        return metricsService.getAllMetrics();
    }

    @GetMapping("/{id}")
    public Metrics getMetricById(@PathVariable Long id) {
        return metricsService.getMetricById(id).orElseThrow();
    }

    @PostMapping
    public Metrics createMetric(@RequestBody Metrics metric) {
        return metricsService.saveMetric(metric);
    }

    @PutMapping("/{id}")
    public Metrics updateMetric(@PathVariable Long id, @RequestBody Metrics metricDetails) {
        return metricsService.updateMetric(id, metricDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteMetric(@PathVariable Long id) {
        metricsService.deleteMetric(id);
    }
}
