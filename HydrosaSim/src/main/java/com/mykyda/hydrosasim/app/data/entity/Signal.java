package com.mykyda.hydrosasim.app.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signal")
public class Signal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double azimuth;

    private Long stationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "water_object_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WaterObject waterObject;

    @CreationTimestamp
    private LocalDateTime sentAt;

    private double strength;
}
