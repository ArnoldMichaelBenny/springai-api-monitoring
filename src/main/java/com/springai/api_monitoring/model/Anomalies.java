package com.springai.api_monitoring.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomalies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Anomalies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "api_id")
    private APIs api;

    @ManyToOne
    @JoinColumn(name = "metric_id")
    private Metrics metric;

    private String type;
    private String severity;
    private LocalDateTime detectedAt;
}
