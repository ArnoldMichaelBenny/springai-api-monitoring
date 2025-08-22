package com.springai.api_monitoring.service;

import com.springai.api_monitoring.model.Anomalies;
import com.springai.api_monitoring.repository.AnomaliesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnomaliesService {
    private final AnomaliesRepository anomaliesRepository;

    public AnomaliesService(AnomaliesRepository anomaliesRepository) {
        this.anomaliesRepository = anomaliesRepository;
    }

    public List<Anomalies> getAllAnomalies() {
        return anomaliesRepository.findAll();
    }

    public Optional<Anomalies> getAnomalyById(Long id) {
        return anomaliesRepository.findById(id);
    }

    public Anomalies saveAnomaly(Anomalies anomaly) {
        return anomaliesRepository.save(anomaly);
    }

    public Anomalies updateAnomaly(Long id, Anomalies anomalyDetails) {
        Anomalies anomaly = anomaliesRepository.findById(id).orElseThrow();
        anomaly.setApi(anomalyDetails.getApi());
        anomaly.setMetric(anomalyDetails.getMetric());
        anomaly.setType(anomalyDetails.getType());
        anomaly.setSeverity(anomalyDetails.getSeverity());
        anomaly.setDetectedAt(anomalyDetails.getDetectedAt());
        return anomaliesRepository.save(anomaly);
    }

    public void deleteAnomaly(Long id) {
        anomaliesRepository.deleteById(id);
    }
}
