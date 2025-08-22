package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.APIs;
import com.springai.api_monitoring.model.Metrics;
import com.springai.api_monitoring.repository.APIsRepository;
import com.springai.api_monitoring.repository.AnomaliesRepository;
import com.springai.api_monitoring.repository.MetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnomaliesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private APIsRepository apisRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private AnomaliesRepository anomaliesRepository;

    @BeforeEach
    void setUp() {
        // Clean the database before each test to ensure isolation
        anomaliesRepository.deleteAll();
        metricsRepository.deleteAll();
        apisRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /detect should trigger detection and create an anomaly for an anomalous metric")
    void testManualDetectionFlow_whenAnomalyExists() throws Exception {
        // 1. Arrange: Create an API and a metric that will cause an anomaly
        APIs testApi = apisRepository.save(APIs.builder().name("Test API").url("http://test.com").build());
        metricsRepository.save(Metrics.builder().api(testApi).responseTime(5000f).errorRate(0.01f).timestamp(LocalDateTime.now()).build());

        // 2. Act: Trigger the detection endpoint via an HTTP POST request
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                // Assert on structured data, not a brittle string message
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.anomaliesFound").value(1));

        // 3. Assert: Verify that one anomaly was created in the database
        assertThat(anomaliesRepository.count()).isEqualTo(1);
        assertThat(anomaliesRepository.findAll().get(0).getType()).isEqualTo("Slow Response Time");
    }

    @Test
    @DisplayName("POST /detect should find no anomalies for healthy metrics")
    void testManualDetectionFlow_whenMetricsAreHealthy() throws Exception {
        // 1. Arrange: Create an API and a healthy metric (below thresholds)
        APIs testApi = apisRepository.save(APIs.builder().name("Healthy API").url("http://healthy.com").build());
        metricsRepository.save(Metrics.builder()
                .api(testApi)
                .responseTime(200f) // Well below 800ms threshold
                .errorRate(0.01f)   // Well below 0.1 threshold
                .timestamp(LocalDateTime.now())
                .build());

        // 2. Act: Trigger the detection endpoint
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.anomaliesFound").value(0));

        // 3. Assert: Verify that no anomalies were created
        assertThat(anomaliesRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /detect should find no anomalies when no metrics exist")
    void testManualDetectionFlow_whenNoMetricsExist() throws Exception {
        // 1. Arrange: No data is created, the database is empty

        // 2. Act: Trigger the detection endpoint
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.anomaliesFound").value(0));

        // 3. Assert: Verify that the anomalies table is still empty
        assertThat(anomaliesRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /detect should create a CRITICAL anomaly for high error rate")
    void testManualDetectionFlow_whenCriticalErrorRateAnomalyExists() throws Exception {
        // 1. Arrange: Create a metric with a high error rate
        APIs testApi = apisRepository.save(APIs.builder().name("Error API").url("http://error.com").build());
        metricsRepository.save(Metrics.builder()
                .api(testApi)
                .responseTime(200f)
                .errorRate(0.5f) // Exceeds 0.1 threshold
                .timestamp(LocalDateTime.now())
                .build());

        // 2. Act & Assert on response
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.anomaliesFound").value(1));

        // 3. Assert on database state
        assertThat(anomaliesRepository.count()).isEqualTo(1);
        var anomaly = anomaliesRepository.findAll().get(0);
        assertThat(anomaly.getType()).isEqualTo("High Error Rate");
        assertThat(anomaly.getSeverity()).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("POST /detect should create one CRITICAL anomaly when both thresholds are exceeded")
    void testManualDetectionFlow_whenBothThresholdsExceeded() throws Exception {
        // 1. Arrange: Create a metric that is both slow and has a high error rate
        APIs testApi = apisRepository.save(APIs.builder().name("Failing API").url("http://failing.com").build());
        metricsRepository.save(Metrics.builder()
                .api(testApi)
                .responseTime(1500f) // Exceeds
                .errorRate(0.5f)    // Exceeds
                .timestamp(LocalDateTime.now())
                .build());

        // 2. Act & Assert on response
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anomaliesFound").value(1));

        // 3. Assert on database state
        assertThat(anomaliesRepository.count()).isEqualTo(1);
        var anomaly = anomaliesRepository.findAll().get(0);
        assertThat(anomaly.getType()).isEqualTo("High Error Rate; Slow Response Time");
        assertThat(anomaly.getSeverity()).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("POST /detect should not create duplicate anomalies on subsequent runs")
    void testManualDetectionFlow_shouldNotCreateDuplicateAnomalies() throws Exception {
        // 1. Arrange: Create an anomalous metric
        APIs testApi = apisRepository.save(APIs.builder().name("Test API").url("http://test.com").build());
        metricsRepository.save(Metrics.builder().api(testApi).responseTime(5000f).timestamp(LocalDateTime.now()).build());

        // 2. Act (First Run): Trigger detection, expect 1 new anomaly
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anomaliesFound").value(1));

        // 3. Assert (First Run): Verify one anomaly exists
        assertThat(anomaliesRepository.count()).isEqualTo(1);

        // 4. Act (Second Run): Trigger detection again, expect 0 new anomalies
        mockMvc.perform(post("/api/anomalies/detect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anomaliesFound").value(0));

        // 5. Assert (Second Run): Verify the total count is still 1
        assertThat(anomaliesRepository.count()).isEqualTo(1);
    }
}