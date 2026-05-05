package com.mykyda.hydrosa.app.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracked_object")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    private Double estimatedSpeed;

    private Double estimatedDirection;

    private LocalDateTime lastSeen;

    private LocalDateTime firstSeen;

    private Integer detectionCount;

    private Double confidence;

    @Enumerated(EnumType.STRING)
    private TrackStatus status;
}