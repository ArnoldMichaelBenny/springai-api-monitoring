package com.springai.api_monitoring.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "api_id")
    private APIs api;

    @ManyToOne
    @JoinColumn(name = "anomaly_id")
    private Anomalies anomaly;

    private String actionType;
    private String status;
    private LocalDateTime executedAt;
}
