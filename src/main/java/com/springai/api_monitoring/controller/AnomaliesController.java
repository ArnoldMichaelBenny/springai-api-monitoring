package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.Anomalies;
import com.springai.api_monitoring.service.AnomaliesService;
import com.springai.api_monitoring.service.AnomalyDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> triggerManualDetection() {
        // Define a time window for the manual scan, e.g., the last 10 minutes.
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10);
        int count = anomalyDetectionService.detectAnomalies(startTime, endTime);

        // Return a structured response instead of a simple string
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", "SUCCESS");
        responseBody.put("anomaliesFound", count);
        responseBody.put("scanWindowStart", startTime.toString());
        responseBody.put("scanWindowEnd", endTime.toString());
        return ResponseEntity.ok(responseBody);
    }
}
