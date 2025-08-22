package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.Anomalies;
import com.springai.api_monitoring.service.AnomaliesService;
import com.springai.api_monitoring.service.AnomalyDetectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
public class AnomaliesController {

    private final AnomaliesService anomaliesService;
    private final AnomalyDetectionService anomalyDetectionService;

    public AnomaliesController(AnomaliesService anomaliesService,
                               AnomalyDetectionService anomalyDetectionService) {
        this.anomaliesService = anomaliesService;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @GetMapping
    public List<Anomalies> getAllAnomalies() {
        return anomaliesService.getAllAnomalies();
    }

    @GetMapping("/{id}")
    public Anomalies getAnomalyById(@PathVariable Long id) {
        return anomaliesService.getAnomalyById(id).orElseThrow();
    }

    @PostMapping
    public Anomalies createAnomaly(@RequestBody Anomalies anomaly) {
        return anomaliesService.saveAnomaly(anomaly);
    }

    @PutMapping("/{id}")
    public Anomalies updateAnomaly(@PathVariable Long id, @RequestBody Anomalies anomalyDetails) {
        return anomaliesService.updateAnomaly(id, anomalyDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteAnomaly(@PathVariable Long id) {
        anomaliesService.deleteAnomaly(id);
    }

    // New endpoint to trigger anomaly detection
    @PostMapping("/detect")
    public String detectAnomalies() {
        int count = anomalyDetectionService.detectAnomalies();
        return count + " anomalies detected and added.";
    }
}
