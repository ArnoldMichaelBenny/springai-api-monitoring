package com.springai.api_monitoring.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "apis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String owner;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
