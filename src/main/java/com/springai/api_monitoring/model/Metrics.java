package com.springai.api_monitoring.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "api_id")
    private APIs api;

    private Float responseTime;
    private Float errorRate;
    private Integer throughput;
    private LocalDateTime timestamp;
}
